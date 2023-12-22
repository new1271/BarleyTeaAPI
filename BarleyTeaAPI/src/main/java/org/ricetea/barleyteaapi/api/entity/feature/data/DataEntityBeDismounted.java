package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.utils.Lazy;
import org.spigotmc.event.entity.EntityDismountEvent;

import javax.annotation.Nonnull;

public final class DataEntityBeDismounted extends BaseEntityFeatureData<EntityDismountEvent> {
    @Nonnull
    private final Lazy<DataEntityType> passengerType;

    public DataEntityBeDismounted(@Nonnull EntityDismountEvent event) {
        super(event, event.getDismounted());
        passengerType = Lazy.create(() -> BaseEntity.getEntityType(getPassenger()));
    }

    @Nonnull
    public Entity getPassenger() {
        return event.getDismounted();
    }

    @Nonnull
    public DataEntityType getPassengerType() {
        return passengerType.get();
    }
}
