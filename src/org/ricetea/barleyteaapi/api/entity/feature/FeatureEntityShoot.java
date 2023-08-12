package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileHitBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileHitEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileLaunch;

public interface FeatureEntityShoot {
    boolean handleEntityShoot(@Nonnull DataProjectileLaunch data);

    boolean handleShotEntity(@Nonnull DataProjectileHitEntity data);

    boolean handleShotBlock(@Nonnull DataProjectileHitBlock data);
}
