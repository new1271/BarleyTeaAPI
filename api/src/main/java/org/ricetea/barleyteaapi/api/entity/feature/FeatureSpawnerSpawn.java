package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataSpawnerSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataSpawnerSpawnPosibility;
import org.ricetea.barleyteaapi.api.entity.feature.state.StateEntitySpawn;

import javax.annotation.Nonnull;

public interface FeatureSpawnerSpawn extends EntityFeature {
    @Nonnull
    StateEntitySpawn handleSpawnerSpawn(@Nonnull DataSpawnerSpawn data);

    double getSpawnerSpawnPosibility(@Nonnull DataSpawnerSpawnPosibility data);
}
