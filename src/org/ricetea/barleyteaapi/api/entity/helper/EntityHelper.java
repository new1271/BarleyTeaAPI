package org.ricetea.barleyteaapi.api.entity.helper;

import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;

public final class EntityHelper {

    @Nullable
    public static Entity getProjectileShooterEntity(Projectile projectile) {
        ProjectileSource source = projectile.getShooter();
        if (source instanceof Entity) {
            return (Entity) source;
        } else {
            return null;
        }
    }

    public static boolean isPlayer(DataEntityType entityType) {
        EntityType type = entityType.getEntityTypeForMinecraftBuiltInMob();
        return type != null && type.equals(EntityType.PLAYER);
    }
}
