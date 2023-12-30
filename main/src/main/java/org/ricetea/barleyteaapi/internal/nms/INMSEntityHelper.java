package org.ricetea.barleyteaapi.internal.nms;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ApiStatus.Internal
public interface INMSEntityHelper extends IHelper {
    boolean damage(@Nonnull Entity damagee, @Nullable Entity damager,
                   @Nullable EntityDamageEvent.DamageCause cause, float damage, boolean withoutScaling);
}
