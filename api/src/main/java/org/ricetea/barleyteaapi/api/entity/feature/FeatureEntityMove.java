package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMove;

import javax.annotation.Nonnull;

public interface FeatureEntityMove {
    boolean handleEntityMove(@Nonnull DataEntityMove data);
}
