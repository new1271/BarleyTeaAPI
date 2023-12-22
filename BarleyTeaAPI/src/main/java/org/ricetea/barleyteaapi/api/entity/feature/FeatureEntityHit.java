package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityHit;

import javax.annotation.Nonnull;

public interface FeatureEntityHit {
    boolean handleEntityHit(@Nonnull DataEntityHit data);
}
