package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMove;

public interface FeatureEntityMove {
    boolean handleEntityMove(@Nonnull DataEntityMove data);
}
