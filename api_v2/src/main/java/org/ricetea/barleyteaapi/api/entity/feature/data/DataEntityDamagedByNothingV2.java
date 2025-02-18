package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.damage.DamageSource;
import org.bukkit.event.entity.EntityDamageEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
public final class DataEntityDamagedByNothingV2 extends DataEntityDamagedByNothing {

    public DataEntityDamagedByNothingV2(@Nonnull EntityDamageEvent event) {
        super(event);
    }

    @Nonnull
    public DamageSource getDamageSource() {
        return event.getDamageSource();
    }
}
