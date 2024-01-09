package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataNaturalSpawnPosibility;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.state.StateNaturalSpawn;

import javax.annotation.Nonnull;

public interface FeatureNaturalSpawn {
    @Nonnull
    StateNaturalSpawn handleNaturalSpawn(@Nonnull DataNaturalSpawn data);

    double getSpawnPosibility(@Nonnull DataNaturalSpawnPosibility data);
}
