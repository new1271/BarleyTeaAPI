package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;
import org.spigotmc.event.entity.EntityDismountEvent;

import javax.annotation.Nonnull;

public final class DataEntityBeDismounted extends BaseEntityFeatureData<EntityDismountEvent> {
    @Nonnull
    private final Lazy<CustomEntityType> passengerType;

    public DataEntityBeDismounted(@Nonnull EntityDismountEvent event) {
        super(event, event.getDismounted());
        passengerType = Lazy.create(() -> CustomEntityType.get(getPassenger()));
    }

    @Nonnull
    public Entity getPassenger() {
        return event.getDismounted();
    }

    @Nonnull
    public CustomEntityType getPassengerType() {
        return passengerType.get();
    }
}
