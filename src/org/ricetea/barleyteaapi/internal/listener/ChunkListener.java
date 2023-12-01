package org.ricetea.barleyteaapi.internal.listener;

import java.util.Iterator;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.ricetea.barleyteaapi.api.block.BaseBlock;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockTick;
import org.ricetea.barleyteaapi.api.block.registration.BlockRegister;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTick;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.internal.chunk.ChunkStorage;
import org.ricetea.barleyteaapi.internal.task.BlockTickTask;
import org.ricetea.barleyteaapi.internal.task.EntityTickTask;
import org.ricetea.utils.Lazy;

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
        if (event == null)
            return;
        EntityRegister register = EntityRegister.getInstanceUnsafe();
        if (register != null && register.hasAnyRegistered()) {
            for (Iterator<Entity> iterator = event.getEntities().iterator(); iterator.hasNext();) {
                Entity entity = iterator.next();
                if (entity != null) {
                    NamespacedKey id = BaseEntity.getEntityID(entity);
                    if (id != null) {
                        BaseEntity entityType = register.lookup(id);
                        try {
                            if (entityType instanceof org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityLoad entityChunkLoad) {
                                entityChunkLoad.handleEntityLoaded(entity);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (entityType instanceof FeatureEntityTick) {
                            EntityTickTask.getInstance().addEntity(entity);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenEntitiesUnload(EntitiesUnloadEvent event) {
        if (event == null)
            return;
        EntityRegister register = EntityRegister.getInstanceUnsafe();
        if (register != null && register.hasAnyRegistered()) {
            for (Iterator<Entity> iterator = event.getEntities().iterator(); iterator.hasNext();) {
                Entity entity = iterator.next();
                if (entity != null) {
                    NamespacedKey id = BaseEntity.getEntityID(entity);
                    if (id != null) {
                        BaseEntity entityType = register.lookup(id);
                        try {
                            if (entityType instanceof org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityLoad entityChunkLoad) {
                                entityChunkLoad.handleEntityUnloaded(entity);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (entityType instanceof FeatureEntityTick) {
                            EntityTickTask.getInstance().removeEntity(entity);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenChunkLoad(ChunkLoadEvent event) {
        if (event == null)
            return;
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (register != null && register.hasAnyRegistered()) {
            for (var entry : ChunkStorage.getBlockDataContainersFromChunk(Objects.requireNonNull(event.getChunk()))) {
                Block block = entry.getKey();
                if (block != null) {
                    NamespacedKey id = BaseBlock.getBlockID(block);
                    if (id != null) {
                        BaseBlock blockType = register.lookup(id);
                        try {
                            if (blockType instanceof org.ricetea.barleyteaapi.api.block.feature.FeatureBlockLoad blockChunkLoad) {
                                blockChunkLoad.handleBlockLoaded(block);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (blockType instanceof FeatureBlockTick) {
                            BlockTickTask.getInstance().addBlock(block);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenChunkUnload(ChunkUnloadEvent event) {
        if (event == null)
            return;
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (register != null && register.hasAnyRegistered()) {
            for (var entry : ChunkStorage.getBlockDataContainersFromChunk(Objects.requireNonNull(event.getChunk()))) {
                Block block = entry.getKey();
                if (block != null) {
                    NamespacedKey id = BaseBlock.getBlockID(block);
                    if (id != null) {
                        BaseBlock blockType = register.lookup(id);
                        try {
                            if (blockType instanceof org.ricetea.barleyteaapi.api.block.feature.FeatureBlockLoad blockChunkLoad) {
                                blockChunkLoad.handleBlockUnloaded(block);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (blockType instanceof FeatureBlockTick) {
                            BlockTickTask.getInstance().removeBlock(block);
                        }
                    }
                }
            }
        }
    }
}
