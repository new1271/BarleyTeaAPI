package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.damage.DamageSource;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
public final class DataEntityAttackV2 extends DataEntityAttack {

    public DataEntityAttackV2(@Nonnull EntityDamageByEntityEvent event) {
        super(event);
    }

    @Nonnull
    public DamageSource getDamageSource() {
        return event.getDamageSource();
    }
}
