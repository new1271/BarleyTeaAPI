package org.ricetea.barleyteaapi.api.entity.helper;

import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class EntityHelper {

    @Nullable
    public static Entity getProjectileShooterEntity(@Nullable Projectile projectile) {
        return ObjectUtil.tryCast(ObjectUtil.callWhenNonnull(projectile, Projectile::getShooter), Entity.class);
    }

    public static boolean isPlayer(@Nullable DataEntityType entityType) {
        if (entityType == null)
            return false;
        else {
            EntityType type = entityType.getEntityTypeForMinecraftBuiltInMob();
            return type != null && type.equals(EntityType.PLAYER);
        }
    }
}
