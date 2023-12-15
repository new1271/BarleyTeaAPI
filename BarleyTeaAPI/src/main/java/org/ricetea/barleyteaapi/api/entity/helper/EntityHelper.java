package org.ricetea.barleyteaapi.api.entity.helper;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.internal.nms.INMSEntityHelper;
import org.ricetea.barleyteaapi.internal.nms.NMSHelperRegister;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nullable;

public final class EntityHelper {

    @Nullable
    public static Entity getProjectileShooterEntity(@Nullable Projectile projectile) {
        return ObjectUtil.tryCast(ObjectUtil.safeMap(projectile, Projectile::getShooter), Entity.class);
    }

    public static boolean isPlayer(@Nullable DataEntityType entityType) {
        if (entityType == null)
            return false;
        else {
            return entityType.isPlayer();
        }
    }

    public static boolean damage(@Nullable Entity damagee, @Nullable DamageCause damageCause, float damage) {
        return damage(damagee, null, damageCause, damage);
    }

    public static boolean damage(@Nullable Entity damagee, @Nullable Entity damager, @Nullable DamageCause damageCause,
                                 float damage) {
        if (damagee == null)
            return false;
        INMSEntityHelper helper = NMSHelperRegister.getHelper(INMSEntityHelper.class);
        if (helper == null)
            return false;
        return helper.damage(damagee, damager, damageCause, damage, true);
    }

    public static boolean damageWithDifficultyScaling(@Nullable Entity damagee, @Nullable DamageCause damageCause,
                                                      float damage) {
        return damageWithDifficultyScaling(damagee, null, damageCause, damage);
    }

    public static boolean damageWithDifficultyScaling(@Nullable Entity damagee, @Nullable Entity damager,
                                                      @Nullable DamageCause damageCause, float damage) {
        if (damagee == null)
            return false;
        INMSEntityHelper helper = NMSHelperRegister.getHelper(INMSEntityHelper.class);
        if (helper == null)
            return false;
        return helper.damage(damagee, damager, damageCause, damage, false);
    }
}
