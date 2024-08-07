package org.ricetea.barleyteaapi.internal.listener;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.barleyteaapi.api.block.helper.BlockHelper;
import org.ricetea.barleyteaapi.api.block.registration.BlockRegister;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.api.internal.chunk.ChunkStorage;
import org.ricetea.barleyteaapi.api.internal.entity.EntityHelperInternals;
import org.ricetea.barleyteaapi.internal.linker.BlockFeatureLinker;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.utils.CollectionUtil;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class ChunkListener implements Listener {
    private static final Lazy<ChunkListener> inst = Lazy.create(ChunkListener::new);

    private ChunkListener() {
    }

    @Nonnull
    public static ChunkListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenEntitiesLoad(EntityAddToWorldEvent event) {
        if (event == null || !EntityRegister.hasRegistered())
            return;
        Entity entity = event.getEntity();
        CustomEntity entityType = CustomEntity.get(entity);
        if (entityType == null)
            return;
        EntityFeatureLinker.loadEntity(entityType, entity, false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenEntitiesUnload(EntityRemoveFromWorldEvent event) {
        if (event == null)
            return;
        Entity entity = event.getEntity();
        EntityHelperInternals internals = EntityHelperInternals.getInstanceUnsafe();
        if (internals != null) {
            internals.removeCachedEntity(entity);
        }
        if (!EntityRegister.hasRegistered())
            return;
        CustomEntity entityType = CustomEntity.get(entity);
        if (entityType == null)
            return;
        EntityFeatureLinker.unloadEntity(entityType, entity);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenChunkLoad(ChunkLoadEvent event) {
        if (event == null)
            return;
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (register == null || register.isEmpty())
            return;
        CollectionUtil.forEach(ChunkStorage.getInstance().getBlockDataContainersFromChunk(event.getChunk()),
                (block, container) -> {
                    if (block == null)
                        return;
                    NamespacedKey id = BlockHelper.getBlockID(container);
                    CustomBlock blockType = register.lookup(id);
                    if (blockType == null)
                        return;
                    BlockFeatureLinker.loadBlock(blockType, block, false);
                });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenChunkUnload(ChunkUnloadEvent event) {
        if (event == null)
            return;
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (register == null || register.isEmpty())
            return;
        CollectionUtil.forEach(ChunkStorage.getInstance().getBlockDataContainersFromChunk(event.getChunk()),
                (block, container) -> {
                    if (block == null)
                        return;
                    NamespacedKey id = BlockHelper.getBlockID(container);
                    CustomBlock blockType = register.lookup(id);
                    if (blockType == null)
                        return;
                    BlockFeatureLinker.unloadBlock(blockType, block);
                });
    }
}
