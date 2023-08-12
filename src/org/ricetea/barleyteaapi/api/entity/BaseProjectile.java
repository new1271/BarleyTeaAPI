package org.ricetea.barleyteaapi.api.entity;

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
import org.ricetea.barleyteaapi.api.entity.feature.FeatureCommandSummon;

public abstract class BaseProjectile extends BaseEntity implements FeatureCommandSummon {

    public BaseProjectile(@Nonnull NamespacedKey key, @Nonnull EntityType entityTypeBasedOn) throws Exception {
        super(key, checkEntityType(entityTypeBasedOn));
    }

    @Nonnull
    private static EntityType checkEntityType(@Nonnull EntityType entityTypeBasedOn) throws Exception {
        if (!Projectile.class.isAssignableFrom(entityTypeBasedOn.getEntityClass()))
            throw new Exception("BaseProjectile cannot be used on non-projectile class!");
        return entityTypeBasedOn;
    }

    @Nullable
    public Projectile spawn(@Nullable Location location) {
        return spawn(location, null);
    }

    @Nullable
    public Projectile spawn(@Nullable Location location, @Nullable ProjectileSource shooter) {
        if (location == null)
            return null;
        World world = location.getWorld();
        if (world == null)
            return null;
        Projectile entity = (Projectile) world.spawnEntity(location, getEntityTypeBasedOn(), SpawnReason.CUSTOM);
        if (entity == null)
            return null;
        entity.setShooter(shooter);
        spawn(entity);
        return entity;
    }

    protected abstract void spawn(@Nonnull Projectile projectile);

    @Override
    public boolean handleCommandSummon(@Nonnull Entity entity, @Nullable String nbt) {
        if (entity instanceof Projectile) {
            spawn((Projectile) entity);
            return true;
        }
        return false;
    }
}
