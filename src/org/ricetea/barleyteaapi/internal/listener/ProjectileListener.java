package org.ricetea.barleyteaapi.internal.listener;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityHit;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityShoot;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureProjectile;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityHit;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityShoot;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityShotBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityShotEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileHitBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileHitEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileLaunch;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.internal.helper.EntityFeatureHelper;
import org.ricetea.barleyteaapi.util.Lazy;

public final class ProjectileListener implements Listener {
    private static final Lazy<ProjectileListener> inst = new Lazy<>(ProjectileListener::new);

    private ProjectileListener() {
    }

    @Nonnull
    public static ProjectileListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenProjectileLaunch(ProjectileLaunchEvent event) {
        if (event == null || event.isCancelled())
            return;
        Projectile entity = event.getEntity();
        Entity shooter = EntityHelper.getProjectileShooterEntity(entity);
        if (!EntityFeatureHelper.doFeatureCancellable(shooter, event, FeatureEntityShoot.class,
                FeatureEntityShoot::handleEntityShoot, DataEntityShoot::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureHelper.doFeatureCancellable(entity, event, FeatureProjectile.class,
                FeatureProjectile::handleProjectileLaunch, DataProjectileLaunch::new)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenProjectileHit(ProjectileHitEvent event) {
        if (event == null || event.isCancelled())
            return;
        if (event.getHitEntity() != null) {
            onProjectileHitEntity(event);
        }
        if (event.isCancelled())
            return;
        if (event.getHitBlock() != null) {
            onProjectileHitBlock(event);
        }
    }

    private void onProjectileHitEntity(@Nonnull ProjectileHitEvent event) {
        Projectile entity = event.getEntity();
        Entity shooter = EntityHelper.getProjectileShooterEntity(entity);
        if (!EntityFeatureHelper.doFeatureCancellable(shooter, event, FeatureEntityShoot.class,
                FeatureEntityShoot::handleShotEntity, DataEntityShotEntity::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureHelper.doFeatureCancellable(entity, event, FeatureProjectile.class,
                FeatureProjectile::handleProjectileHitEntity, DataProjectileHitEntity::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureHelper.doFeatureCancellable(event.getHitEntity(), event, FeatureEntityHit.class,
                FeatureEntityHit::handleEntityHit, DataEntityHit::new)) {
            event.setCancelled(true);
            return;
        }
    }

    private void onProjectileHitBlock(@Nonnull ProjectileHitEvent event) {
        Projectile entity = event.getEntity();
        Entity shooter = EntityHelper.getProjectileShooterEntity(entity);
        if (!EntityFeatureHelper.doFeatureCancellable(shooter, event, FeatureEntityShoot.class,
                FeatureEntityShoot::handleShotBlock, DataEntityShotBlock::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureHelper.doFeatureCancellable(entity, event, FeatureProjectile.class,
                FeatureProjectile::handleProjectileHitBlock, DataProjectileHitBlock::new)) {
            event.setCancelled(true);
            return;
        }
    }
}
