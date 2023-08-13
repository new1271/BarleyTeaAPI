package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataKillEntity extends BaseEntityFeatureData<EntityDeathEvent> {
    @Nonnull
    private final Lazy<DataEntityType> decedentType;

    public DataKillEntity(@Nonnull EntityDeathEvent event,
            @Nonnull EntityDamageByEntityEvent lastDamageCauseByEntityEvent) {
        super(event, lastDamageCauseByEntityEvent.getDamager());
        decedentType = new Lazy<>(() -> BaseEntity.getEntityType(getDecedent()));
    }

    @SuppressWarnings("null")
    @Nonnull
    public Entity getDecedent() {
        return event.getEntity();
    }

    @Nonnull
    public DataEntityType getDecedentType() {
        return decedentType.get();
    }
}
