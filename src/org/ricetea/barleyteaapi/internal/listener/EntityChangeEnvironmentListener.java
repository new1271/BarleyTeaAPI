package org.ricetea.barleyteaapi.internal.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.annotation.Nonnull;

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
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.api.block.BaseBlock;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockBreak;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockEntityChange;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockFalling;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockTick;
import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockBreakByEntityExplode;
import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockDropByEntity;
import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockEntityChange;
import org.ricetea.barleyteaapi.api.block.registration.BlockRegister;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityExplode;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityExplode;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.internal.chunk.ChunkStorage;
import org.ricetea.barleyteaapi.internal.entity.BarleyFallingBlock;
import org.ricetea.barleyteaapi.internal.helper.EntityFeatureHelper;
import org.ricetea.barleyteaapi.internal.task.BlockTickTask;
import org.ricetea.barleyteaapi.util.Lazy;

public final class EntityChangeEnvironmentListener implements Listener {
    private static final Lazy<EntityChangeEnvironmentListener> inst = new Lazy<>(
            EntityChangeEnvironmentListener::new);

    private EntityChangeEnvironmentListener() {
    }

    @Nonnull
    public static EntityChangeEnvironmentListener getInstance() {
        return inst.get();
    }

    private final HashMap<Entity, ArrayList<BaseBlock>> PrepareToDrops = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenEntityExplode(EntityExplodeEvent event) {
        if (event == null || event.isCancelled())
            return;
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (register != null) {
            if (!EntityFeatureHelper.doFeatureCancellable(event.getEntity(), event, FeatureEntityExplode.class,
                    FeatureEntityExplode::handleEntityExplode, DataEntityExplode::new)) {
                event.setCancelled(true);
                return;
            }
            for (var iterator = event.blockList().iterator(); iterator.hasNext();) {
                Block block = iterator.next();
                if (block != null) {
                    NamespacedKey id = BaseBlock.getBlockID(block);
                    if (id != null) {
                        BaseBlock baseBlock = register.lookup(id);
                        if (baseBlock != null) {
                            if (baseBlock instanceof FeatureBlockBreak blockBreakFeature) {
                                if (!blockBreakFeature
                                        .handleBlockBreakByEntityExplode(
                                                new DataBlockBreakByEntityExplode(event, block))) {
                                    iterator.remove();
                                    continue;
                                } else if (event.isCancelled()) {
                                    return;
                                }
                            }
                            if (baseBlock instanceof FeatureBlockTick) {
                                BlockTickTask.getInstance().removeBlock(block);
                            }
                            ChunkStorage.removeBlockDataContainer(block);
                            if (baseBlock instanceof FeatureBlockBreak) {
                                var list = PrepareToDrops.get(event.getEntity());
                                if (list == null) {
                                    PrepareToDrops.put(event.getEntity(), list = new ArrayList<>());
                                }
                                list.add(baseBlock);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event == null || event.isCancelled())
            return;
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        Block block = event.getBlock();
        Entity entity = event.getEntity();
        EntityType entityType = event.getEntityType();
        if (entityType != null && entity != null) {
            switch (entityType) {
                case FALLING_BLOCK:
                    NamespacedKey key = BaseEntity.getEntityID(entity);
                    if (key == null) {
                        if (register != null && block != null) {
                            NamespacedKey blockKey = BaseBlock.getBlockID(block);
                            if (blockKey != null) {
                                BaseBlock baseBlock = register.lookup(blockKey);
                                if (baseBlock != null) {
                                    if (baseBlock instanceof FeatureBlockFalling blockFallingFeature
                                            && !blockFallingFeature.handleBlockStartFall(block)) {
                                        event.setCancelled(true);
                                        return;
                                    }
                                    BaseEntity.registerEntity(entity, BarleyFallingBlock.getInstance());
                                    PersistentDataContainer container = ChunkStorage.getBlockDataContainer(block,
                                            false);
                                    if (container != null)
                                        BarleyFallingBlock.setBlockDataContainer(entity, container);
                                    if (baseBlock instanceof FeatureBlockTick) {
                                        BlockTickTask.getInstance().removeBlock(block);
                                    }
                                    ChunkStorage.removeBlockDataContainer(block);
                                }
                            }
                        }
                    } else {
                        EntityRegister entityRegister = EntityRegister.getInstanceUnsafe();
                        if (entityRegister != null
                                && BarleyFallingBlock.getInstance().equals(entityRegister.lookup(key))) {
                            PersistentDataContainer container = BarleyFallingBlock.getBlockDataContainer(entity);
                            if (container != null) {
                                String rawId = container.get(BaseBlock.BlockTagNamespacedKey,
                                        PersistentDataType.STRING);
                                if (rawId != null) {
                                    NamespacedKey id = NamespacedKey.fromString(rawId);
                                    if (register != null && id != null && block != null) {
                                        BaseBlock baseBlock = register.lookup(key);
                                        PersistentDataContainer previousDataContainer = ChunkStorage
                                                .getBlockDataContainer(block,
                                                        false);
                                        ChunkStorage.setBlockDataContainer(block, container);
                                        if (baseBlock instanceof FeatureBlockFalling blockFallingFeature
                                                && !blockFallingFeature.handleBlockFallToGround(block)) {
                                            event.setCancelled(true);
                                            ChunkStorage.setBlockDataContainer(block, previousDataContainer);
                                            return;
                                        }
                                        if (baseBlock instanceof FeatureBlockTick) {
                                            BlockTickTask.getInstance().addBlock(block);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                default:
                    if (register != null && block != null) {
                        NamespacedKey id = BaseBlock.getBlockID(block);
                        if (id != null) {
                            BaseBlock baseBlock = register.lookup(id);
                            if (baseBlock != null) {
                                if (baseBlock instanceof FeatureBlockEntityChange entityChangeBlockFeature) {
                                    if (!entityChangeBlockFeature
                                            .handleBlockEntityChange(
                                                    new DataBlockEntityChange(event))
                                            || event.isCancelled()) {
                                        return;
                                    }
                                }
                                if (baseBlock instanceof FeatureBlockTick) {
                                    BlockTickTask.getInstance().removeBlock(block);
                                }
                                ChunkStorage.removeBlockDataContainer(block);
                                if (baseBlock instanceof FeatureBlockBreak && entityType.equals(EntityType.WITHER)) {
                                    var list = PrepareToDrops.get(event.getEntity());
                                    if (list == null) {
                                        PrepareToDrops.put(event.getEntity(), list = new ArrayList<>());
                                    }
                                    list.add(baseBlock);

                                }
                            }
                        }
                    }
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenEntityDropItem(EntityDropItemEvent event) {
        if (event == null || event.isCancelled())
            return;
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        Entity entity = event.getEntity();
        EntityType entityType = event.getEntityType();
        if (entityType != null && entity != null) {
            switch (entityType) {
                case FALLING_BLOCK:
                    NamespacedKey key = BaseEntity.getEntityID(entity);
                    if (key != null) {
                        EntityRegister entityRegister = EntityRegister.getInstanceUnsafe();
                        if (entityRegister != null
                                && BarleyFallingBlock.getInstance().equals(entityRegister.lookup(key))) {
                            PersistentDataContainer container = BarleyFallingBlock.getBlockDataContainer(entity);
                            if (container != null) {
                                String rawId = container.get(BaseBlock.BlockTagNamespacedKey,
                                        PersistentDataType.STRING);
                                if (rawId != null) {
                                    NamespacedKey id = NamespacedKey.fromString(rawId);
                                    if (register != null && id != null) {
                                        if (register.lookup(key) instanceof FeatureBlockFalling blockFallingFeature &&
                                                !blockFallingFeature.handleBlockFallDropItem(container,
                                                        Objects.requireNonNull(event.getItemDrop()))
                                                || event.isCancelled()) {
                                            event.setCancelled(true);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                default:
                    Item item = event.getItemDrop();
                    if (item != null) {
                        ItemStack itemStack = item.getItemStack();
                        if (itemStack != null) {
                            var list = PrepareToDrops.get(event.getEntity());
                            if (list != null) {
                                for (var iterator = list.iterator(); iterator.hasNext();) {
                                    BaseBlock baseBlock = iterator.next();
                                    if (baseBlock.getBlockTypeBasedOn().equals(itemStack.getType())
                                            && baseBlock instanceof FeatureBlockBreak blockBreakFeature) {
                                        iterator.remove();
                                        boolean result = blockBreakFeature
                                                .handleBlockDropByEntity(new DataBlockDropByEntity(event));
                                        if (event.isCancelled())
                                            return;
                                        if (!result) {
                                            event.setCancelled(true);
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
                    break;
            }
        }
    }
}
