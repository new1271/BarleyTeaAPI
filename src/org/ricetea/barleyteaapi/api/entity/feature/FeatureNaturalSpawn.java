package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

public interface FeatureNaturalSpawn {
    @Nonnull
    StateNaturalSpawn handleNaturalSpawn(DataNaturalSpawn data);

    double getPosibility();
}
