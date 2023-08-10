package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileHitBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileHitEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileLaunch;

public interface FeatureProjectile {
    boolean handleProjectileLaunch(DataProjectileLaunch data);

    boolean handleProjectileHitEntity(DataProjectileHitEntity data);

    boolean handleProjectileHitBlock(DataProjectileHitBlock data);
}
