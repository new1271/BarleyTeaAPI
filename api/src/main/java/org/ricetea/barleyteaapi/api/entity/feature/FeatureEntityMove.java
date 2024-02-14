package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMove;

import javax.annotation.Nonnull;

public interface FeatureEntityMove extends EntityFeature {
    boolean handleEntityMove(@Nonnull DataEntityMove data);
}
