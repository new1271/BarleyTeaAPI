package org.ricetea.barleyteaapi.internal.listener;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.DataCreatureNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.util.Lazy;

public final class CreatureSpawnListener implements Listener {
    private static final Lazy<CreatureSpawnListener> inst = new Lazy<>(CreatureSpawnListener::new);

    private CreatureSpawnListener() {
    }

    public static CreatureSpawnListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenCreatureSpawn(CreatureSpawnEvent event) {
        if (event == null || event.isCancelled())
            return;
        Random rnd = ThreadLocalRandom.current();
        for (BaseEntity entityType : EntityRegister.getInstance()
                .getEntityTypes(e -> e.getEntityTypeBasedOn().equals(event.getEntityType())
                        && e instanceof FeatureNaturalSpawn)) {
            FeatureNaturalSpawn spawnEntityType = (FeatureNaturalSpawn) entityType;
            if (rnd.nextDouble() < spawnEntityType.getPosibility()) {
                if (!spawnEntityType.handleNaturalSpawn(new DataCreatureNaturalSpawn(event))) {
                    event.setCancelled(true);
                }
                break;
            }
        }
    }
}
