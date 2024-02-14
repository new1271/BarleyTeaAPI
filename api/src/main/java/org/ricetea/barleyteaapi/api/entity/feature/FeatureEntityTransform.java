package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTransform;

import javax.annotation.Nonnull;

public interface FeatureEntityTransform extends EntityFeature {
    boolean handleEntityTransform(@Nonnull DataEntityTransform data);
}
