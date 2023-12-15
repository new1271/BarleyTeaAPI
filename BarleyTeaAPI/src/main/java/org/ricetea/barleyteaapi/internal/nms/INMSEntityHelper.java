package org.ricetea.barleyteaapi.internal.nms;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface INMSEntityHelper extends IHelper {
    boolean damage(@Nonnull Entity damagee, @Nullable Entity damager,
                   @Nullable EntityDamageEvent.DamageCause cause, float damage, boolean withoutScaling);
}
