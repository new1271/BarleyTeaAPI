package org.ricetea.barleyteaapi.api.entity.feature.data;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.ricetea.barleyteaapi.api.abstracts.DataEntityBase;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class DataEntityDeath extends DataEntityBase<EntityDeathEvent> {
    @Nullable
    private final Entity killer;

    @Nonnull
    private final Lazy<DataEntityType> decedentType;

    @Nullable
    private final Lazy<DataEntityType> killerType;

    @SuppressWarnings("null")
    public DataEntityDeath(@Nonnull EntityDeathEvent event,
            @Nullable EntityDamageByEntityEvent lastDamageCauseByEntityEvent) {
        super(event);
        decedentType = new Lazy<>(() -> BaseEntity.getEntityType(event.getEntity()));
        killer = ObjectUtil.callWhenNonnull(lastDamageCauseByEntityEvent, EntityDamageByEntityEvent::getDamager);
        killerType = ObjectUtil.callWhenNonnull(killer,
                (Function<Entity, Lazy<DataEntityType>>) killer -> new Lazy<>(
                        () -> BaseEntity.getEntityType(killer)));
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

    @Nullable
    public Entity getKiller() {
        return killer;
    }

    @Nullable
    public DataEntityType getKillerType() {
        Lazy<DataEntityType> killerType = this.killerType;
        if (killerType == null)
            return null;
        else
            return killerType.get();
    }

    public boolean hasKiller() {
        return killer != null;
    }
}
