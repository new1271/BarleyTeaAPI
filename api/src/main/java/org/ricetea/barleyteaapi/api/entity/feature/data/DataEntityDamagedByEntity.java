package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;

@SuppressWarnings("deprecation")
public class DataEntityDamagedByEntity extends BaseEntityFeatureData<EntityDamageByEntityEvent> {
    @Nonnull
    private final Lazy<CustomEntityType> damagerType;

    public DataEntityDamagedByEntity(@Nonnull EntityDamageByEntityEvent event) {
        super(event);
        damagerType = Lazy.create(() -> CustomEntityType.get(getDamager()));
    }

    @Nonnull
    public Entity getDamager() {
        return event.getDamager();
    }

    @Nonnull
    public CustomEntityType getDamagerType() {
        return damagerType.get();
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

    public double getFinalDamage() {
        return event.getFinalDamage();
    }

    @Nonnull
    public EntityDamageEvent.DamageCause getDamageCause() {
        return event.getCause();
    }

    public boolean isCritical() {
        return event.isCritical();
    }
}
