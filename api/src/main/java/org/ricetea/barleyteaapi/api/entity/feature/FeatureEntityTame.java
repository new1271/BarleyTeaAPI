package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTame;

import javax.annotation.Nonnull;

public interface FeatureEntityTame extends EntityFeature {
    boolean handleEntityTame(@Nonnull DataEntityTame data);
}
