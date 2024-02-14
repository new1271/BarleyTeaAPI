package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileHitBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileHitEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileLaunch;

import javax.annotation.Nonnull;

public interface FeatureProjectile extends EntityFeature {
    boolean handleProjectileLaunch(@Nonnull DataProjectileLaunch data);

    boolean handleProjectileHitEntity(@Nonnull DataProjectileHitEntity data);

    boolean handleProjectileHitBlock(@Nonnull DataProjectileHitBlock data);
}
