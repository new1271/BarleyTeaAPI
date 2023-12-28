package org.ricetea.barleyteaapi.api.entity.template;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.projectiles.ProjectileSource;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.entity.feature.*;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataCommandSummon;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileLaunch;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.internal.task.EntityTickTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BaseProjectile extends DefaultEntity
        implements FeatureCommandSummon, FeatureProjectileSpawn, FeatureProjectile {

    public BaseProjectile(@Nonnull NamespacedKey key, @Nonnull EntityType entityTypeBasedOn) {
        super(key, checkEntityType(key, entityTypeBasedOn));
    }

    @Nonnull
    private static EntityType checkEntityType(@Nonnull NamespacedKey key, @Nonnull EntityType entityTypeBasedOn) {
        Class<? extends Entity> clazz = entityTypeBasedOn.getEntityClass();
        if (clazz != null && !Projectile.class.isAssignableFrom(clazz)) {
            BarleyTeaAPI.warnWhenPluginUsable(
                    "BaseProjectile cannot be used on non-projectile entity type! (trigger at " + key + ")");
        }
        return entityTypeBasedOn;
    }

    @Nullable
    public Projectile handleEntitySpawn(@Nullable Location location) {
        return handleEntitySpawn(location, null);
    }

    @Nullable
    public Projectile handleEntitySpawn(@Nullable Location location, @Nullable ProjectileSource shooter) {
        if (location == null)
            return null;
        World world = location.getWorld();
        if (world == null)
            return null;
        Projectile entity = (Projectile) world.spawnEntity(location, getOriginalType(), SpawnReason.CUSTOM);
        entity.setShooter(shooter);
        if (EntityHelper.tryRegister(this, entity, this::handleEntitySpawn)) {
            if (this instanceof FeatureEntityLoad feature) {
                feature.handleEntityLoaded(entity);
            }
            if (this instanceof FeatureEntityTick) {
                EntityTickTask.getInstance().addEntity(entity);
            }
            return entity;
        } else {
            entity.remove();
        }
        return null;
    }

    protected abstract boolean handleEntitySpawn(@Nonnull Projectile projectile);

    @Override
    public boolean handleCommandSummon(@Nonnull DataCommandSummon data) {
        if (data.getEntity() instanceof Projectile projectile) {
            return EntityHelper.tryRegister(this, projectile, this::handleEntitySpawn);
        }
        return false;
    }

    @Override
    public boolean handleProjectileLaunch(@Nonnull DataProjectileLaunch data) {
        return EntityHelper.tryRegister(this, data.getEntity(), this::handleEntitySpawn);
    }
}
