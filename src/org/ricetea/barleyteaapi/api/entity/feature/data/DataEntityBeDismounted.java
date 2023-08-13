package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;
import org.spigotmc.event.entity.EntityDismountEvent;

public final class DataEntityBeDismounted extends BaseEntityFeatureData<EntityDismountEvent> {
    @Nonnull
    private final Lazy<DataEntityType> passengerType;

    public DataEntityBeDismounted(@Nonnull EntityDismountEvent event) {
        super(event, event.getDismounted());
        passengerType = new Lazy<>(() -> BaseEntity.getEntityType(getPassenger()));
    }

    @SuppressWarnings("null")
    @Nonnull
    public Entity getPassenger() {
        return event.getDismounted();
    }

    @Nonnull
    public DataEntityType getPassengerType() {
        return passengerType.get();
    }
}
