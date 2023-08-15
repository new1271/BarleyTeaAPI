package org.ricetea.barleyteaapi.api.entity.template;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.projectiles.ProjectileSource;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureCommandSummon;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureProjectile;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureProjectileSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileLaunch;

public abstract class BaseProjectile extends BaseEntity
        implements FeatureCommandSummon, FeatureProjectileSpawn, FeatureProjectile {

    public BaseProjectile(@Nonnull NamespacedKey key, @Nonnull EntityType entityTypeBasedOn) throws Exception {
        super(key, checkEntityType(entityTypeBasedOn));
    }

    @Nonnull
    private static EntityType checkEntityType(@Nonnull EntityType entityTypeBasedOn) throws Exception {
        if (!Projectile.class.isAssignableFrom(entityTypeBasedOn.getEntityClass()))
            throw new Exception("BaseProjectile cannot be used on non-projectile entity type!");
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
        Projectile entity = (Projectile) world.spawnEntity(location, getEntityTypeBasedOn(), SpawnReason.CUSTOM);
        if (entity == null)
            return null;
        entity.setShooter(shooter);
        if (tryRegister(entity, this::handleEntitySpawn)) {
            return entity;
        } else {
            entity.remove();
        }
        return null;
    }

    protected abstract boolean handleEntitySpawn(@Nonnull Projectile projectile);

    @Override
    public boolean handleCommandSummon(@Nonnull Entity entity, @Nullable String nbt) {
        if (entity instanceof Projectile projectile) {
            return tryRegister(projectile, this::handleEntitySpawn);
        }
        return false;
    }

    @Override
    public boolean handleProjectileLaunch(@Nonnull DataProjectileLaunch data) {
        return tryRegister(data.getEntity(), this::handleEntitySpawn);
    }
}
