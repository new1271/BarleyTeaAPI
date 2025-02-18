package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
public final class DataEntityDamagedByEntityV2 extends DataEntityDamagedByEntity {

    public DataEntityDamagedByEntityV2(@Nonnull EntityDamageByEntityEvent event) {
        super(event);
    }

    @Nonnull
    public DamageSource getDamageSource() {
        return event.getDamageSource();
    }
}
