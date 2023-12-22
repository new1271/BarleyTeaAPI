package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;

public final class DataEntityAttack extends BaseEntityFeatureData<EntityDamageByEntityEvent> {
    @Nonnull
    private final Lazy<DataEntityType> damageeType;

    public DataEntityAttack(@Nonnull EntityDamageByEntityEvent event) {
        super(event, event.getDamager());
        damageeType = Lazy.create(() -> BaseEntity.getEntityType(getDamagee()));
    }

    @Nonnull
    public Entity getDamagee() {
        return event.getEntity();
    }

    @Nonnull
    public DataEntityType getDamageeType() {
        return damageeType.get();
    }

    public double getDamage() {
        return event.getDamage();
    }

    public void setDamage(double damage) {
        event.setDamage(damage);
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
