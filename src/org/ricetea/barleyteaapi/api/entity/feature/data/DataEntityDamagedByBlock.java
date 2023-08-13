package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;

public final class DataEntityDamagedByBlock extends BaseEntityFeatureData<EntityDamageByBlockEvent> {

    public DataEntityDamagedByBlock(@Nonnull EntityDamageByBlockEvent event) {
        super(event);
    }

    @SuppressWarnings("null")
    @Nonnull
    public Block getDamager() {
        return event.getDamager();
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
