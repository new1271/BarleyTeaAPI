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
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTick;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.internal.task.EntityTickTask;
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
        EntityRegister register = EntityRegister.getInstance();
        if (register.hasAnyRegisteredMob()) {
            for (Iterator<Entity> iterator = event.getEntities().iterator(); iterator.hasNext();) {
                Entity entity = iterator.next();
                if (entity != null) {
                    NamespacedKey id = BaseEntity.getEntityID(entity);
                    if (id != null) {
                        BaseEntity entityType = register.lookupEntityType(id);
                        if (entityType instanceof FeatureChunkLoad entityChunkLoad) {
                            entityChunkLoad.handleChunkLoaded(entity);
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
        EntityRegister register = EntityRegister.getInstance();
        if (register.hasAnyRegisteredMob()) {
            for (Iterator<Entity> iterator = event.getEntities().iterator(); iterator.hasNext();) {
                Entity entity = iterator.next();
                if (entity != null) {
                    NamespacedKey id = BaseEntity.getEntityID(entity);
                    if (id != null) {
                        BaseEntity entityType = register.lookupEntityType(id);
                        if (entityType instanceof FeatureChunkLoad entityChunkLoad) {
                            entityChunkLoad.handleChunkUnloaded(entity);
                        }
                        if (entityType instanceof FeatureEntityTick) {
                            EntityTickTask.getInstance().removeEntity(entity);
                        }
                    }
                }
            }
        }
    }
}
