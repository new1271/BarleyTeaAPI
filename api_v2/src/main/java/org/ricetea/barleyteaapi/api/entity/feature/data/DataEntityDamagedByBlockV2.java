package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.damage.DamageSource;
import org.bukkit.event.entity.EntityDamageByBlockEvent;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
public final class DataEntityDamagedByBlockV2 extends DataEntityDamagedByBlock {

    public DataEntityDamagedByBlockV2(@Nonnull EntityDamageByBlockEvent event) {
        super(event);
    }

    @Nonnull
    public DamageSource getDamageSource() {
        return event.getDamageSource();
    }
}
