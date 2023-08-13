package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class DataEntityDeath extends BaseEntityFeatureData<EntityDeathEvent> {
    @Nullable
    private final Entity killer;

    @Nullable
    private final Lazy<DataEntityType> killerType;

    @SuppressWarnings("null")
    public DataEntityDeath(@Nonnull EntityDeathEvent event,
            @Nullable EntityDamageByEntityEvent lastDamageCauseByEntityEvent) {
        super(event);
        killer = ObjectUtil.mapWhenNonnull(lastDamageCauseByEntityEvent, EntityDamageByEntityEvent::getDamager);
        killerType = ObjectUtil.mapWhenNonnull(killer, killer -> new Lazy<>(() -> BaseEntity.getEntityType(killer)));
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
