package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTransform;

import javax.annotation.Nonnull;

public interface FeatureEntityTransform {
    boolean handleEntityTransform(@Nonnull DataEntityTransform data);
}
