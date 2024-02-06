package org.ricetea.barleyteaapi.api.entity.template;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.projectiles.ProjectileSource;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureCommandSummon;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureProjectile;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureProjectileSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataCommandSummon;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileLaunch;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.api.internal.misc.MiscInternalFunctions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BaseProjectile extends DefaultEntity
        implements FeatureCommandSummon, FeatureProjectileSpawn, FeatureProjectile {

    public BaseProjectile(@Nonnull NamespacedKey key, @Nonnull EntityType entityTypeBasedOn) {
        super(key, checkEntityType(key, entityTypeBasedOn));
    }

    @Nonnull
    private static EntityType checkEntityType(@Nonnull NamespacedKey key, @Nonnull EntityType originalType) {
        Class<? extends Entity> clazz = originalType.getEntityClass();
        if (clazz != null && !Projectile.class.isAssignableFrom(clazz)) {
            new Exception("BaseProjectile cannot be used on non-projectile entity type! (trigger at " + key + ")")
                    .printStackTrace();
        }
        return originalType;
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
        MiscInternalFunctions functions = MiscInternalFunctions.getInstanceUnsafe();
        boolean result;
        if (functions == null) {
            result = EntityHelper.tryRegister(this, entity, this::handleEntitySpawn);
        } else {
            result = functions.tryRegisterEntityAfterSpawn(this, entity, this::handleEntitySpawn);
        }
        return result ? entity : null;
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
