package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityHit;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityShoot;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureProjectile;
import org.ricetea.barleyteaapi.api.entity.feature.data.*;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityShoot;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityShoot;
import org.ricetea.barleyteaapi.internal.helper.EntityFeatureHelper;
import org.ricetea.barleyteaapi.internal.helper.ItemFeatureHelper;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;

public final class ProjectileListener implements Listener {
    private static final Lazy<ProjectileListener> inst = Lazy.create(ProjectileListener::new);

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
        if (!ItemFeatureHelper.forEachHandsCancellable(ObjectUtil.tryCast(shooter, LivingEntity.class), event,
                FeatureItemHoldEntityShoot.class, FeatureItemHoldEntityShoot::handleItemHoldEntityShoot,
                DataItemHoldEntityShoot::new)) {
            event.setCancelled(true);
            return;
        }
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
