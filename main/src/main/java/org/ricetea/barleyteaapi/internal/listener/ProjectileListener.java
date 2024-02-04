package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityHit;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityShoot;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureProjectile;
import org.ricetea.barleyteaapi.api.entity.feature.data.*;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class ProjectileListener implements Listener {
    private static final Lazy<ProjectileListener> inst = Lazy.create(ProjectileListener::new);

    private ProjectileListener() {
    }

    @Nonnull
    public static ProjectileListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void listenProjectileHitFirst(ProjectileHitEvent event) {
        if (event == null || event.isCancelled())
            return;
        if (event.getHitEntity() != null) {
            onProjectileHitEntityFirst(event);
        }
        if (event.isCancelled())
            return;
        if (event.getHitBlock() != null) {
            onProjectileHitBlock(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenProjectileHitLast(ProjectileHitEvent event) {
        if (event == null || event.isCancelled())
            return;
        if (event.getHitEntity() != null) {
            onProjectileHitEntityLast(event);
        }
    }

    private void onProjectileHitEntityFirst(@Nonnull ProjectileHitEvent event) {
        Projectile entity = event.getEntity();
        Entity shooter = EntityHelper.getProjectileShooterEntity(entity);
        if (!EntityFeatureLinker.doFeatureCancellable(shooter, event, FeatureEntityShoot.class,
                FeatureEntityShoot::handleShotEntity, DataEntityShotEntity::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureLinker.doFeatureCancellable(entity, event, FeatureProjectile.class,
                FeatureProjectile::handleProjectileHitEntity, DataProjectileHitEntity::new)) {
            event.setCancelled(true);
            return;
        }
    }

    private void onProjectileHitEntityLast(@Nonnull ProjectileHitEvent event) {
        if (!EntityFeatureLinker.doFeatureCancellable(event.getHitEntity(), event, FeatureEntityHit.class,
                FeatureEntityHit::handleEntityHit, DataEntityHit::new)) {
            event.setCancelled(true);
            return;
        }
    }

    private void onProjectileHitBlock(@Nonnull ProjectileHitEvent event) {
        Projectile entity = event.getEntity();
        Entity shooter = EntityHelper.getProjectileShooterEntity(entity);
        if (!EntityFeatureLinker.doFeatureCancellable(shooter, event, FeatureEntityShoot.class,
                FeatureEntityShoot::handleShotBlock, DataEntityShotBlock::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureLinker.doFeatureCancellable(entity, event, FeatureProjectile.class,
                FeatureProjectile::handleProjectileHitBlock, DataProjectileHitBlock::new)) {
            event.setCancelled(true);
            return;
        }
    }
}
