package org.ricetea.barleyteaapi.api.entity.feature.monitor;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileHitBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileHitEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileLaunch;

import javax.annotation.Nonnull;

public interface FeatureMonitorProjectile extends EntityMonitorFeature {
    void monitorProjectileLaunch(@Nonnull DataProjectileLaunch data);

    void monitorProjectileHitEntity(@Nonnull DataProjectileHitEntity data);

    void monitorProjectileHitBlock(@Nonnull DataProjectileHitBlock data);
}
