package org.ricetea.barleyteaapi.internal.listener;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.state.StateNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.util.Lazy;

public final class CreatureSpawnListener implements Listener {
    private static final Lazy<CreatureSpawnListener> inst = new Lazy<>(CreatureSpawnListener::new);

    private CreatureSpawnListener() {
    }

    @Nonnull
    public static CreatureSpawnListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenCreatureSpawn(CreatureSpawnEvent event) {
        if (event == null || event.isCancelled())
            return;
        SpawnReason reason = event.getSpawnReason();
        if (reason == null || reason.equals(SpawnReason.CUSTOM) || reason.equals(SpawnReason.COMMAND)
                || reason.equals(SpawnReason.DEFAULT))
            return;
        Random rnd = ThreadLocalRandom.current();
        for (BaseEntity entityType : EntityRegister.getInstance()
                .getEntityTypes(e -> e.getEntityTypeBasedOn().equals(event.getEntityType())
                        && e instanceof FeatureNaturalSpawn)) {
            if (entityType != null) {
                FeatureNaturalSpawn spawnEntityType = (FeatureNaturalSpawn) entityType;
                if (spawnEntityType.filterSpawnReason(reason) && rnd.nextDouble() < spawnEntityType.getPosibility()) {
                    StateNaturalSpawn result = spawnEntityType.handleNaturalSpawn(new DataNaturalSpawn(event));
                    switch (result) {
                        case Handled:
                            BaseEntity.registerEntity(event.getEntity(), entityType);
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
