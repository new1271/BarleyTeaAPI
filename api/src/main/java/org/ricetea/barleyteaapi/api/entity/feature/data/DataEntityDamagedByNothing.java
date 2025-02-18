package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.event.entity.EntityDamageEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;

import javax.annotation.Nonnull;

@SuppressWarnings("deprecation")
public class DataEntityDamagedByNothing extends BaseEntityFeatureData<EntityDamageEvent> {
    public DataEntityDamagedByNothing(@Nonnull EntityDamageEvent event) {
        super(event);
    }

    public double getBaseDamage() {
        return event.getDamage(EntityDamageEvent.DamageModifier.BASE);
    }

    public double getDamage(@Nonnull EntityDamageEvent.DamageModifier modifier) {
        return event.getDamage(modifier);
    }

    public double getDamage() {
        return event.getDamage();
    }

    public void setDamage(@Nonnull EntityDamageEvent.DamageModifier modifier, double damage) {
        event.setDamage(modifier, damage);
    }

    public void setDamage(double damage) {
        event.setDamage(damage);
    }

    public void setBaseDamage(double damage) {
        event.setDamage(EntityDamageEvent.DamageModifier.BASE, damage);
    }

    @Nonnull
    public EntityDamageEvent.DamageCause getDamageCause() {
        return event.getCause();
    }
}
