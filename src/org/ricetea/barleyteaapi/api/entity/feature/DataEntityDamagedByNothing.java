package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.ricetea.barleyteaapi.api.abstracts.DataEntityBase;
import org.ricetea.barleyteaapi.api.entity.BarleyTeaEntityType;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataEntityDamagedByNothing extends DataEntityBase<EntityDamageEvent> {
    @Nonnull
    private final Lazy<BarleyTeaEntityType> damageeType;

    public DataEntityDamagedByNothing(@Nonnull EntityDamageEvent event) {
        super(event);
        damageeType = new Lazy<>(() -> BaseEntity.getEntityType(event.getEntity()));
    }

    @SuppressWarnings("null")
    @Nonnull
    public Entity getDamagee() {
        return event.getEntity();
    }

    @Nonnull
    public BarleyTeaEntityType getDamageeType() {
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
}
