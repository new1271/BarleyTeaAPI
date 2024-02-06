package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.event.entity.EntityDamageEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;

import javax.annotation.Nonnull;

public final class DataEntityDamagedByNothing extends BaseEntityFeatureData<EntityDamageEvent> {
    public DataEntityDamagedByNothing(@Nonnull EntityDamageEvent event) {
        super(event);
    }

    public double getDamage() {
        return event.getDamage();
    }

    public void setDamage(double damage) {
        event.setDamage(damage);
    }

    @Nonnull
    public EntityDamageEvent.DamageCause getDamageCause() {
        return event.getCause();
    }
}
