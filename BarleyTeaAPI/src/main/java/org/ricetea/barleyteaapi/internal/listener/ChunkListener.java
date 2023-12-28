package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockLoad;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockTick;
import org.ricetea.barleyteaapi.api.block.helper.BlockHelper;
import org.ricetea.barleyteaapi.api.block.registration.BlockRegister;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityLoad;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTick;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.internal.chunk.ChunkStorage;
import org.ricetea.barleyteaapi.internal.task.BlockTickTask;
import org.ricetea.barleyteaapi.internal.task.EntityTickTask;
import org.ricetea.utils.CollectionUtil;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;

public final class ChunkListener implements Listener {
    private static final Lazy<ChunkListener> inst = Lazy.create(ChunkListener::new);

    private ChunkListener() {
    }

    @Nonnull
    public static ChunkListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenEntitiesLoad(EntitiesLoadEvent event) {
        if (event == null || !EntityRegister.hasRegistered())
            return;
        EntityTickTask task = EntityTickTask.getInstance();
        for (Entity entity : event.getEntities()) {
            CustomEntity entityType = CustomEntity.get(entity);
            if (entityType == null)
                return;
            if (entityType instanceof FeatureEntityLoad feature) {
                try {
                    feature.handleEntityLoaded(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (entityType instanceof FeatureEntityTick) {
                task.addEntity(entity);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenEntitiesUnload(EntitiesUnloadEvent event) {
        if (event == null || !EntityRegister.hasRegistered())
            return;
        EntityTickTask task = EntityTickTask.getInstanceUnsafe();
        for (Entity entity : event.getEntities()) {
            CustomEntity entityType = CustomEntity.get(entity);
            if (entityType == null)
                return;
            if (entityType instanceof FeatureEntityLoad feature) {
                try {
                    feature.handleEntityUnloaded(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (entityType instanceof FeatureEntityTick && task != null) {
                task.removeEntity(entity);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenChunkLoad(ChunkLoadEvent event) {
        if (event == null)
            return;
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (register == null || !register.hasAnyRegistered())
            return;
        BlockTickTask task = BlockTickTask.getInstance();
        CollectionUtil.forEach(ChunkStorage.getBlockDataContainersFromChunk(event.getChunk()),
                (block, container) -> {
                    if (block == null)
                        return;
                    NamespacedKey id = BlockHelper.getBlockID(container);
                    CustomBlock blockType = register.lookup(id);
                    if (blockType == null)
                        return;
                    if (blockType instanceof FeatureBlockLoad feature) {
                        try {
                            feature.handleBlockLoaded(block);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (blockType instanceof FeatureBlockTick) {
                        task.addBlock(block);
                    }
                });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenChunkUnload(ChunkUnloadEvent event) {
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (register == null || !register.hasAnyRegistered())
            return;
        BlockTickTask task = BlockTickTask.getInstanceUnsafe();
        CollectionUtil.forEach(ChunkStorage.getBlockDataContainersFromChunk(event.getChunk()),
                (block, container) -> {
                    if (block == null)
                        return;
                    NamespacedKey id = BlockHelper.getBlockID(container);
                    CustomBlock blockType = register.lookup(id);
                    if (blockType == null)
                        return;
                    if (blockType instanceof FeatureBlockLoad feature) {
                        try {
                            feature.handleBlockUnloaded(block);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (blockType instanceof FeatureBlockTick && task != null) {
                        task.removeBlock(block);
                    }
                });
    }
}
