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
import org.ricetea.barleyteaapi.api.helper.FeatureHelper;
import org.ricetea.barleyteaapi.api.internal.chunk.ChunkStorage;
import org.ricetea.barleyteaapi.api.persistence.ExtraPersistentDataType;
import org.ricetea.barleyteaapi.internal.entity.BarleyFallingBlock;
import org.ricetea.barleyteaapi.internal.linker.BlockFeatureLinker;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
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
            FeatureBlockBreak feature = blockType.getFeature(FeatureBlockBreak.class);
            if (feature != null) {
                try {
                    if (!feature.handleBlockBreakByEntityExplode(new DataBlockBreakByEntityExplode(event, block))) {
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
            BlockFeatureLinker.unloadBlock(blockType, block);
            ChunkStorage.getInstance().removeBlockDataContainer(block);
            var list = PrepareToDrops.computeIfAbsent(event.getEntity(), ignored -> new ArrayList<>());
            list.add(blockType);
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
                    FeatureBlockFalling feature = blockType.getFeature(FeatureBlockFalling.class);
                    if (feature != null) {
                        try {
                            if (!feature.handleBlockStartFall(block)) {
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
                                FeatureBlockFalling feature = blockType.getFeature(FeatureBlockFalling.class);
                                if (feature != null) {
                                    try {
                                        if (!feature.handleBlockFallToGround(block)) {
                                            event.setCancelled(true);
                                            chunkStorage.setBlockDataContainer(block, previousDataContainer);
                                            return;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                BlockFeatureLinker.loadBlock(blockType, block, false);
                            }
                        }
                    }
                }
            }
            default -> {
                CustomBlock blockType = CustomBlock.get(block);
                if (blockType == null)
                    return;
                FeatureBlockEntityChange feature = blockType.getFeature(FeatureBlockEntityChange.class);
                if (feature != null) {
                    try {
                        if (!feature.handleBlockEntityChange(new DataBlockEntityChange(event)) || event.isCancelled()) {
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                BlockFeatureLinker.unloadBlock(blockType, block);
                ChunkStorage.getInstance().removeBlockDataContainer(block);
                if (blockType.getFeature(FeatureBlockBreak.class) != null && entityType.equals(EntityType.WITHER)) {
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
                            FeatureBlockFalling feature = FeatureHelper.getFeatureUnsafe(baseBlock, FeatureBlockFalling.class);
                            if (feature != null) {
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
                        if (baseBlock.getOriginalType().equals(itemStack.getType())) {
                            FeatureBlockBreak feature = baseBlock.getFeature(FeatureBlockBreak.class);
                            if (feature != null) {
                                iterator.remove();
                                try {
                                    boolean result = feature.handleBlockDropByEntity(new DataBlockDropByEntity(event));
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
                    }
                    if (list.isEmpty()) {
                        PrepareToDrops.remove(event.getEntity());
                    }
                }
            }
        }
    }
}
