package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.util.Either;
import org.ricetea.barleyteaapi.util.Lazy;

public final class EntityDamagedByNothingData {
    @Nonnull
    private final EntityDamageEvent event;

    @Nonnull
    private final Lazy<Either<EntityType, BaseEntity>> damageeType;

    public EntityDamagedByNothingData(@Nonnull EntityDamageEvent event) {
        this.event = event;
        damageeType = new Lazy<>(() -> BaseEntity.getEntityType(event.getEntity()));
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

    @Nonnull
    public EntityDamageEvent getBaseEvent() {
        return event;
    }
}
