package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockBreak;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockEntityChange;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockFalling;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockTick;
import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockBreakByEntityExplode;
import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockDropByEntity;
import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockEntityChange;
import org.ricetea.barleyteaapi.api.block.helper.BlockHelper;
import org.ricetea.barleyteaapi.api.block.registration.BlockRegister;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityExplode;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityExplode;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.api.internal.chunk.ChunkStorage;
import org.ricetea.barleyteaapi.api.persistence.ExtraPersistentDataType;
import org.ricetea.barleyteaapi.internal.entity.BarleyFallingBlock;
import org.ricetea.barleyteaapi.internal.linker.BlockFeatureLinker;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.barleyteaapi.internal.task.BlockTickTask;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Singleton
@ApiStatus.Internal
public final class EntityChangeEnvironmentListener implements Listener {
    private static final Lazy<EntityChangeEnvironmentListener> inst = Lazy.create(
            EntityChangeEnvironmentListener::new);
    private final HashMap<Entity, ArrayList<CustomBlock>> PrepareToDrops = new HashMap<>();

    private EntityChangeEnvironmentListener() {
    }

    @Nonnull
    public static EntityChangeEnvironmentListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenEntityExplode(EntityExplodeEvent event) {
        if (event == null || event.isCancelled() || !BlockRegister.hasRegistered())
            return;
        if (!EntityFeatureLinker.doFeatureCancellable(event.getEntity(), event, FeatureEntityExplode.class,
                FeatureEntityExplode::handleEntityExplode, DataEntityExplode::new)) {
            event.setCancelled(true);
            return;
        }
        for (var iterator = event.blockList().iterator(); iterator.hasNext(); ) {
            Block block = iterator.next();
            CustomBlock blockType = CustomBlock.get(block);
            if (blockType == null)
                continue;
            if (blockType instanceof FeatureBlockBreak blockBreakFeature) {
                try {
                    if (!blockBreakFeature
                            .handleBlockBreakByEntityExplode(
                                    new DataBlockBreakByEntityExplode(event, block))) {
                        iterator.remove();
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (event.isCancelled()) {
                    return;
                }
            }
            if (blockType instanceof FeatureBlockTick) {
                BlockTickTask.getInstance().removeBlock(block);
            }
            ChunkStorage.getInstance().removeBlockDataContainer(block);
            if (blockType instanceof FeatureBlockBreak) {
                var list = PrepareToDrops.computeIfAbsent(event.getEntity(), ignored -> new ArrayList<>());
                list.add(blockType);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event == null || event.isCancelled() || !BlockRegister.hasRegistered())
            return;
        Block block = event.getBlock();
        Entity entity = event.getEntity();
        EntityType entityType = event.getEntityType();
        switch (entityType) {
            case FALLING_BLOCK -> {
                NamespacedKey key = EntityHelper.getEntityID(entity);
                ChunkStorage chunkStorage = ChunkStorage.getInstance();
                if (key == null) {
                    CustomBlock blockType = CustomBlock.get(block);
                    if (blockType == null)
                        return;
                    if (blockType instanceof FeatureBlockFalling blockFallingFeature) {
                        try {
                            if (!blockFallingFeature.handleBlockStartFall(block)) {
                                event.setCancelled(true);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    EntityHelper.register(BarleyFallingBlock.getInstance(), entity);
                    PersistentDataContainer container = chunkStorage.getBlockDataContainer(block, false);
                    if (container != null)
                        BarleyFallingBlock.setBlockDataContainer(entity, container);
                    BlockFeatureLinker.unloadBlock(blockType, block);
                    if (blockType instanceof FeatureBlockTick) {
                        BlockTickTask.getInstance().removeBlock(block);
                    }
                    chunkStorage.removeBlockDataContainer(block);
                } else {
                    EntityRegister entityRegister = EntityRegister.getInstanceUnsafe();
                    if (entityRegister != null
                            && BarleyFallingBlock.getInstance().equals(entityRegister.lookup(key))) {
                        PersistentDataContainer container = BarleyFallingBlock.getBlockDataContainer(entity);
                        if (container != null) {
                            NamespacedKey id = container.get(BlockHelper.DefaultNamespacedKey, ExtraPersistentDataType.NAMESPACED_KEY);
                            if (id != null) {
                                CustomBlock blockType = BlockRegister.getInstance().lookup(id);
                                PersistentDataContainer previousDataContainer = chunkStorage.getBlockDataContainer(
                                        block, false);
                                chunkStorage.setBlockDataContainer(block, container);
                                try {
                                    if (blockType instanceof FeatureBlockFalling blockFallingFeature
                                            && !blockFallingFeature.handleBlockFallToGround(block)) {
                                        event.setCancelled(true);
                                        chunkStorage.setBlockDataContainer(block, previousDataContainer);
                                        return;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                BlockFeatureLinker.loadBlock(blockType, block);
                                if (blockType instanceof FeatureBlockTick) {
                                    BlockTickTask.getInstance().addBlock(block);
                                }
                            }
                        }
                    }
                }
            }
            default -> {
                CustomBlock blockType = CustomBlock.get(block);
                if (blockType == null)
                    return;
                if (blockType instanceof FeatureBlockEntityChange feature) {
                    try {
                        if (!feature.handleBlockEntityChange(new DataBlockEntityChange(event)) || event.isCancelled()) {
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                BlockFeatureLinker.unloadBlock(blockType, block);
                if (blockType instanceof FeatureBlockTick) {
                    BlockTickTask.getInstance().removeBlock(block);
                }
                ChunkStorage.getInstance().removeBlockDataContainer(block);
                if (blockType instanceof FeatureBlockBreak && entityType.equals(EntityType.WITHER)) {
                    var list = PrepareToDrops.computeIfAbsent(event.getEntity(), k -> new ArrayList<>());
                    list.add(blockType);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void listenEntityDropItem(EntityDropItemEvent event) {
        if (event == null || event.isCancelled() || !BlockRegister.hasRegistered())
            return;
        Entity entity = event.getEntity();
        EntityType entityType = event.getEntityType();
        switch (entityType) {
            case FALLING_BLOCK -> {
                if (CustomEntity.get(entity) instanceof BarleyFallingBlock) {
                    PersistentDataContainer container = BarleyFallingBlock.getBlockDataContainer(entity);
                    if (container != null) {
                        NamespacedKey id = container.get(BlockHelper.DefaultNamespacedKey, ExtraPersistentDataType.NAMESPACED_KEY);
                        if (id != null) {
                            CustomBlock baseBlock = BlockRegister.getInstance().lookup(id);
                            if (baseBlock instanceof FeatureBlockFalling feature) {
                                try {
                                    if (!feature.handleBlockFallDropItem(container, Objects.requireNonNull(event.getItemDrop()))
                                            || event.isCancelled()) {
                                        event.setCancelled(true);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
            default -> {
                Item item = event.getItemDrop();
                ItemStack itemStack = item.getItemStack();
                var list = PrepareToDrops.get(event.getEntity());
                if (list != null) {
                    for (var iterator = list.iterator(); iterator.hasNext(); ) {
                        CustomBlock baseBlock = iterator.next();
                        if (baseBlock.getOriginalType().equals(itemStack.getType())
                                && baseBlock instanceof FeatureBlockBreak blockBreakFeature) {
                            iterator.remove();
                            try {
                                boolean result = blockBreakFeature
                                        .handleBlockDropByEntity(new DataBlockDropByEntity(event));
                                if (!result) {
                                    event.setCancelled(true);
                                    return;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (event.isCancelled())
                                return;
                        }
                    }
                    if (list.isEmpty()) {
                        PrepareToDrops.remove(event.getEntity());
                    }
                }
            }
        }
    }
}
