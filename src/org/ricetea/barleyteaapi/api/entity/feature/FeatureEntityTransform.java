package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTransform;

public interface FeatureEntityTransform {
    boolean handleEntityTransform(@Nonnull DataEntityTransform data);
}
