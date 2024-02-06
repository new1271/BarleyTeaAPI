package org.ricetea.barleyteaapi.api.entity.template;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.api.internal.misc.MiscInternalFunctions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BaseArrow extends BaseProjectile {

    public BaseArrow(@Nonnull NamespacedKey key, @Nonnull EntityType entityTypeBasedOn) {
        super(key, checkEntityType(key, entityTypeBasedOn));
    }

    @Nonnull
    private static EntityType checkEntityType(@Nonnull NamespacedKey key, @Nonnull EntityType originalType) {
        Class<? extends Entity> clazz = originalType.getEntityClass();
        if (clazz != null && !Arrow.class.isAssignableFrom(clazz)) {
            new Exception("BaseArrow cannot be used on non-arrow entity type! (trigger at " + key + ")")
                    .printStackTrace();
        }
        return originalType;
    }

    @Nullable
    public Arrow handleEntitySpawn(@Nullable Location location, @Nullable Vector vector, float speed,
                                   float spread) {
        return handleEntitySpawn(location, vector, null, speed, spread);
    }

    @Nullable
    public Arrow handleEntitySpawn(@Nullable Location location, @Nullable Vector vector,
                                   @Nullable ProjectileSource shooter, float speed, float spread) {
        if (location == null)
            return null;
        World world = location.getWorld();
        if (world == null)
            return null;
        Arrow entity = world.spawnArrow(location, vector == null ? new Vector(0, 0, 0) : vector, speed, spread);
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
}
