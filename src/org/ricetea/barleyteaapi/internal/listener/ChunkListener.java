package org.ricetea.barleyteaapi.internal.listener;

import java.util.Iterator;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureChunkLoad;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.util.Lazy;

public final class ChunkListener implements Listener {
    private static final Lazy<ChunkListener> inst = new Lazy<>(ChunkListener::new);

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
        for (Iterator<Entity> iterator = event.getEntities().iterator(); iterator.hasNext();) {
            Entity entity = iterator.next();
            NamespacedKey id = BaseEntity.getEntityID(entity);
            if (id != null) {
                BaseEntity entityType = EntityRegister.getInstance().lookupEntityType(id);
                if (entityType != null && entityType instanceof FeatureChunkLoad) {
                    FeatureChunkLoad entityChunkLoad = (FeatureChunkLoad) entity;
                    entityChunkLoad.handleChunkLoaded(entity);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenEntitiesUnload(EntitiesUnloadEvent event) {
        if (event == null)
            return;
        for (Iterator<Entity> iterator = event.getEntities().iterator(); iterator.hasNext();) {
            Entity entity = iterator.next();
            NamespacedKey id = BaseEntity.getEntityID(entity);
            if (id != null) {
                BaseEntity entityType = EntityRegister.getInstance().lookupEntityType(id);
                if (entityType != null && entityType instanceof FeatureChunkLoad) {
                    FeatureChunkLoad entityChunkLoad = (FeatureChunkLoad) entity;
                    entityChunkLoad.handleChunkUnloaded(entity);
                }
            }
        }
    }
}