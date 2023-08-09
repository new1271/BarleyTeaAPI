package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.ricetea.barleyteaapi.api.abstracts.DataEntityBase;
import org.ricetea.barleyteaapi.api.entity.BarleyTeaEntityType;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.util.Either;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataEntityDamagedByEntity extends DataEntityBase<EntityDamageByEntityEvent> {
    @Nonnull
    private final Lazy<BarleyTeaEntityType> damagerType, damageeType;

    public DataEntityDamagedByEntity(@Nonnull EntityDamageByEntityEvent event) {
        super(event);
        damagerType = new Lazy<>(() -> BaseEntity.getEntityType(event.getDamager()));
        damageeType = new Lazy<>(() -> BaseEntity.getEntityType(event.getEntity()));
    }

    @SuppressWarnings("null")
    @Nonnull
    public Entity getDamager() {
        return event.getDamager();
    }

    @Nonnull
    public Either<EntityType, BaseEntity> getDamagerType() {
        return damagerType.get();
    }

    @SuppressWarnings("null")
    @Nonnull
    public Entity getDamagee() {
        return event.getEntity();
    }

    @Nonnull
    public Either<EntityType, BaseEntity> getDamageeType() {
        return damageeType.get();
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

    public boolean isCritical() {
        return event.isCritical();
    }
}
