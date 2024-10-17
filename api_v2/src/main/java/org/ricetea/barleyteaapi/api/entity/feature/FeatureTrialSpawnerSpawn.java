package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataTrialSpawnerSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataTrialSpawnerSpawnPosibility;
import org.ricetea.barleyteaapi.api.entity.feature.state.StateEntitySpawn;

import javax.annotation.Nonnull;

public interface FeatureTrialSpawnerSpawn extends EntityFeature {
    @Nonnull
    StateEntitySpawn handleTrialSpawnerSpawn(@Nonnull DataTrialSpawnerSpawn data);

    double getTrialSpawnerSpawnPosibility(@Nonnull DataTrialSpawnerSpawnPosibility data);
}
