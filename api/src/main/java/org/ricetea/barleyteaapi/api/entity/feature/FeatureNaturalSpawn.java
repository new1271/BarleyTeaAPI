package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataNaturalSpawnPosibility;
import org.ricetea.barleyteaapi.api.entity.feature.state.StateEntitySpawn;

import javax.annotation.Nonnull;

public interface FeatureNaturalSpawn extends EntityFeature {
    @Nonnull
    StateEntitySpawn handleNaturalSpawn(@Nonnull DataNaturalSpawn data);

    double getNaturalSpawnPosibility(@Nonnull DataNaturalSpawnPosibility data);
}
