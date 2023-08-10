package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataNaturalSpawn;

public interface FeatureNaturalSpawn {
    @Nonnull
    StateNaturalSpawn handleNaturalSpawn(DataNaturalSpawn data);

    double getPosibility();

    boolean filterSpawnReason(SpawnReason reason);
}
