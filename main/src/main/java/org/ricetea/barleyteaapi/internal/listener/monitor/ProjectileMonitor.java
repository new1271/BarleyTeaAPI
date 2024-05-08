package org.ricetea.barleyteaapi.internal.listener.monitor;

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
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorEntityHit;
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorEntityShoot;
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorProjectile;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class ProjectileMonitor implements Listener {
    private static final Lazy<ProjectileMonitor> inst = Lazy.create(ProjectileMonitor::new);

    private ProjectileMonitor() {
    }

    @Nonnull
    public static ProjectileMonitor getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void listenProjectileHitFirst(ProjectileHitEvent event) {
        if (event == null || event.isCancelled())
            return;
        if (event.getHitEntity() != null) {
            onProjectileHitEntity(event);
        }
        if (event.getHitBlock() != null) {
            onProjectileHitBlock(event);
        }
    }

    private void onProjectileHitEntity(@Nonnull ProjectileHitEvent event) {
        Projectile entity = event.getEntity();
        Entity shooter = EntityHelper.getProjectileShooterEntity(entity);
        EntityFeatureLinker.doFeature(shooter, event, FeatureMonitorEntityShoot.class,
                FeatureMonitorEntityShoot::monitorShotEntity, DataEntityShotEntity::new);
        EntityFeatureLinker.doFeature(entity, event, FeatureMonitorProjectile.class,
                FeatureMonitorProjectile::monitorProjectileHitEntity, DataProjectileHitEntity::new);
        EntityFeatureLinker.doFeature(event.getHitEntity(), event, FeatureMonitorEntityHit.class,
                FeatureMonitorEntityHit::monitorEntityHit, DataEntityHit::new);
    }

    private void onProjectileHitBlock(@Nonnull ProjectileHitEvent event) {
        Projectile entity = event.getEntity();
        Entity shooter = EntityHelper.getProjectileShooterEntity(entity);
        EntityFeatureLinker.doFeature(shooter, event, FeatureMonitorEntityShoot.class,
                FeatureMonitorEntityShoot::monitorShotBlock, DataEntityShotBlock::new);
        EntityFeatureLinker.doFeature(entity, event, FeatureMonitorProjectile.class,
                FeatureMonitorProjectile::monitorProjectileHitBlock, DataProjectileHitBlock::new);
    }
}
