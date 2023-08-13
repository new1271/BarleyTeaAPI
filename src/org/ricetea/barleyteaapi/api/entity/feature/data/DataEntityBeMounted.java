package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;
import org.spigotmc.event.entity.EntityMountEvent;

public final class DataEntityBeMounted extends BaseEntityFeatureData<EntityMountEvent> {
    @Nonnull
    private final Lazy<DataEntityType> passengerType;

    public DataEntityBeMounted(@Nonnull EntityMountEvent event) {
        super(event, event.getMount());
        passengerType = new Lazy<>(() -> BaseEntity.getEntityType(getPassenger()));
    }

    @SuppressWarnings("null")
    @Nonnull
    public Entity getPassenger() {
        return event.getEntity();
    }

    @Nonnull
    public DataEntityType getPassengerType() {
        return passengerType.get();
    }
}
