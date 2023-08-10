package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.ricetea.barleyteaapi.api.abstracts.DataEntityBase;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataKillEntity extends DataEntityBase<EntityDeathEvent> {
    @Nonnull
    private final Entity killer;

    @Nonnull
    private final Lazy<DataEntityType> decedentType;

    @Nonnull
    private final Lazy<DataEntityType> killerType;

    @SuppressWarnings("null")
    public DataKillEntity(@Nonnull EntityDeathEvent event,
            @Nonnull EntityDamageByEntityEvent lastDamageCauseByEntityEvent) {
        super(event);
        decedentType = new Lazy<>(() -> BaseEntity.getEntityType(event.getEntity()));
        killer = lastDamageCauseByEntityEvent.getDamager();
        killerType = new Lazy<>(() -> BaseEntity.getEntityType(killer));
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

    @Nonnull
    public Entity getKiller() {
        return killer;
    }

    @Nonnull
    public DataEntityType getKillerType() {
        return killerType.get();
    }
}
