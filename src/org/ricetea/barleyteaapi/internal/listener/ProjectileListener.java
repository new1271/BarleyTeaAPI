package org.ricetea.barleyteaapi.internal.listener;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityHit;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityShoot;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureProjectile;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileHitBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileHitEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileLaunch;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
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
        NamespacedKey id = BaseEntity.getEntityID(event.getEntity());
        if (id != null) {
            BaseEntity entityType = EntityRegister.getInstance().lookupEntityType(id);
            if (entityType instanceof FeatureProjectile projectileEntityType) {
                boolean cancelled = !projectileEntityType.handleProjectileLaunch(new DataProjectileLaunch(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        id = BaseEntity.getEntityID(EntityHelper.getProjectileShooterEntity(event.getEntity()));
        if (id != null) {
            BaseEntity entityType = EntityRegister.getInstance().lookupEntityType(id);
            if (entityType instanceof FeatureEntityShoot shootEntityType) {
                boolean cancelled = !shootEntityType.handleEntityShoot(new DataProjectileLaunch(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
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
        NamespacedKey id = BaseEntity.getEntityID(event.getEntity());
        if (id != null) {
            BaseEntity entityType = EntityRegister.getInstance().lookupEntityType(id);
            if (entityType instanceof FeatureProjectile projectileEntityType) {
                boolean cancelled = !projectileEntityType.handleProjectileHitEntity(new DataProjectileHitEntity(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        id = BaseEntity.getEntityID(EntityHelper.getProjectileShooterEntity(event.getEntity()));
        if (id != null) {
            BaseEntity entityType = EntityRegister.getInstance().lookupEntityType(id);
            if (entityType instanceof FeatureEntityShoot shootEntityType) {
                boolean cancelled = !shootEntityType.handleShotEntity(new DataProjectileHitEntity(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        id = BaseEntity.getEntityID(event.getHitEntity());
        if (id != null) {
            BaseEntity entityType = EntityRegister.getInstance().lookupEntityType(id);
            if (entityType instanceof FeatureEntityHit hitEntityType) {
                boolean cancelled = !hitEntityType.handleEntityHit(new DataProjectileHitEntity(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    private void onProjectileHitBlock(@Nonnull ProjectileHitEvent event) {
        NamespacedKey id = BaseEntity.getEntityID(event.getEntity());
        if (id != null) {
            BaseEntity entityType = EntityRegister.getInstance().lookupEntityType(id);
            if (entityType instanceof FeatureProjectile projectileEntityType) {
                boolean cancelled = !projectileEntityType.handleProjectileHitBlock(new DataProjectileHitBlock(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        id = BaseEntity.getEntityID(EntityHelper.getProjectileShooterEntity(event.getEntity()));
        if (id != null) {
            BaseEntity entityType = EntityRegister.getInstance().lookupEntityType(id);
            if (entityType instanceof FeatureEntityShoot shootEntityType) {
                boolean cancelled = !shootEntityType.handleShotBlock(new DataProjectileHitBlock(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
