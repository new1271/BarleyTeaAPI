package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.state.StateNaturalSpawn;

public interface FeatureNaturalSpawn {
    @Nonnull
    StateNaturalSpawn handleNaturalSpawn(@Nonnull DataNaturalSpawn data);

    double getSpawnPosibility(@Nonnull SpawnReason reason);
}
