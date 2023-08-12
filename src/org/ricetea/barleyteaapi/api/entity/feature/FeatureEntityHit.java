package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileHitEntity;

public interface FeatureEntityHit {
    boolean handleEntityHit(@Nonnull DataProjectileHitEntity data);
}
