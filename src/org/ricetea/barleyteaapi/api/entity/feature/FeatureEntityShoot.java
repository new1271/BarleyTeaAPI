package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileHitBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileHitEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileLaunch;

public interface FeatureEntityShoot {
    boolean handleEntityShoot(DataProjectileLaunch data);

    boolean handleShotEntity(DataProjectileHitEntity data);

    boolean handleShotBlock(DataProjectileHitBlock data);
}
