package org.ricetea.barleyteaapi.internal.listener;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureChunkLoad;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTick;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.state.StateNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.internal.task.EntityTickTask;
import org.ricetea.utils.Lazy;

public final class EntitySpawnListener implements Listener {
    private static final Lazy<EntitySpawnListener> inst = Lazy.create(EntitySpawnListener::new);

    private EntitySpawnListener() {
    }

    @Nonnull
    public static EntitySpawnListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenEntitySpawn(EntitySpawnEvent event) {
        if (event == null || event.isCancelled())
            return;
        Entity entity = event.getEntity();
        if (entity != null) {
            if (event instanceof CreatureSpawnEvent creatureSpawnEvent) {
                onCreatureSpawn(creatureSpawnEvent);
            }
            if (!event.isCancelled()) {
                NamespacedKey id = BaseEntity.getEntityID(entity);
                EntityRegister register = EntityRegister.getInstanceUnsafe();
                if (register != null && id != null) {
                    BaseEntity baseEntity = register.lookup(id);
                    if (baseEntity instanceof FeatureEntityTick) {
                        EntityTickTask.getInstance().addEntity(entity);
                    }
                }
            }
        }
    }

    private void onCreatureSpawn(@Nonnull CreatureSpawnEvent event) {
        SpawnReason reason = event.getSpawnReason();
        EntityRegister register = EntityRegister.getInstanceUnsafe();
        if (reason == null || register == null || reason.equals(SpawnReason.CUSTOM)
                || reason.equals(SpawnReason.COMMAND) || reason.equals(SpawnReason.DEFAULT))
            return;
        Random rnd = ThreadLocalRandom.current();
        for (BaseEntity entityType : register.listAll(e -> e instanceof FeatureNaturalSpawn
                && e.getEntityTypeBasedOn().equals(event.getEntityType()))) {
            if (entityType != null) {
                FeatureNaturalSpawn spawnEntityType = (FeatureNaturalSpawn) entityType;
                double posibility = spawnEntityType.getSpawnPosibility(reason);
                if (posibility > 0 && (posibility >= 1 || rnd.nextDouble() < posibility)) {
                    StateNaturalSpawn result = spawnEntityType.handleNaturalSpawn(new DataNaturalSpawn(event));
                    switch (result) {
                        case Handled:
                            if (entityType instanceof FeatureChunkLoad feature) {
                                Entity entity = event.getEntity();
                                if (!entity.isDead())
                                    feature.handleChunkLoaded(entity);
                            }
                            return;
                        case Cancelled:
                            event.setCancelled(true);
                            return;
                        case Skipped:
                            break;
                    }
                }
            }
        }
    }
}
