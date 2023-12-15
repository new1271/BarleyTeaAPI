package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileHitBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileHitEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileLaunch;

public interface FeatureProjectile {
    boolean handleProjectileLaunch(@Nonnull DataProjectileLaunch data);

    boolean handleProjectileHitEntity(@Nonnull DataProjectileHitEntity data);

    boolean handleProjectileHitBlock(@Nonnull DataProjectileHitBlock data);
}
