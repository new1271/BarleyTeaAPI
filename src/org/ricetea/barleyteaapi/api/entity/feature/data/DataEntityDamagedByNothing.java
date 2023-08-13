package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.event.entity.EntityDamageEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;

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

    @SuppressWarnings("null")
    @Nonnull
    public EntityDamageEvent.DamageCause getDamageCause() {
        return event.getCause();
    }
}
