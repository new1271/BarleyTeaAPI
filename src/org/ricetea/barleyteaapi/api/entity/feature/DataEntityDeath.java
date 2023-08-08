package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.ricetea.barleyteaapi.api.entity.BarleyTeaEntityType;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataEntityDeath {
    @Nonnull
    private final EntityDeathEvent event;

    @Nullable
    private final Entity killer;

    @Nonnull
    private final Lazy<BarleyTeaEntityType> decedentType;

    @Nullable
    private final Lazy<BarleyTeaEntityType> killerType;

    public DataEntityDeath(@Nonnull EntityDeathEvent event,
            @Nullable EntityDamageByEntityEvent lastDamageCauseByEntityEvent) {
        this.event = event;
        decedentType = new Lazy<>(() -> BaseEntity.getEntityType(event.getEntity()));
        if (lastDamageCauseByEntityEvent == null) {
            killer = null;
            killerType = null;
        } else {
            killer = lastDamageCauseByEntityEvent.getDamager();
            killerType = new Lazy<>(() -> BaseEntity.getEntityType(killer));
        }
    }

    @SuppressWarnings("null")
    @Nonnull
    public Entity getDecedent() {
        return event.getEntity();
    }

    @Nonnull
    public BarleyTeaEntityType getDecedentType() {
        return decedentType.get();
    }

    @Nullable
    public Entity getKiller() {
        return killer;
    }

    @Nullable
    public BarleyTeaEntityType getKillerType() {
        Lazy<BarleyTeaEntityType> killerType = this.killerType;
        if (killerType == null)
            return null;
        else
            return killerType.get();
    }

    public boolean hasKiller() {
        return killer != null;
    }

    @Nonnull
    public EntityDeathEvent getBaseEvent() {
        return event;
    }
}
