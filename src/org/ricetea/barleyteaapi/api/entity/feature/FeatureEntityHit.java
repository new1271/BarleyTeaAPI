package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileHitEntity;

public interface FeatureEntityHit {
    boolean handleEntityHit(DataProjectileHitEntity data);
}
