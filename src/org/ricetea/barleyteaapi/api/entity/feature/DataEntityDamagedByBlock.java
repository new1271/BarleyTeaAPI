package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.ricetea.barleyteaapi.api.entity.BarleyTeaEntityType;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataEntityDamagedByBlock {
    @Nonnull
    private final EntityDamageByBlockEvent event;

    @Nonnull
    private final Lazy<BarleyTeaEntityType> damageeType;

    public DataEntityDamagedByBlock(@Nonnull EntityDamageByBlockEvent event) {
        this.event = event;
        damageeType = new Lazy<>(() -> BaseEntity.getEntityType(event.getEntity()));
    }

    @SuppressWarnings("null")
    @Nonnull
    public Block getDamager() {
        return event.getDamager();
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

    @Nonnull
    public EntityDamageByBlockEvent getBaseEvent() {
        return event;
    }
}
