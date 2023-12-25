package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;
import org.spigotmc.event.entity.EntityMountEvent;

import javax.annotation.Nonnull;

public final class DataEntityBeMounted extends BaseEntityFeatureData<EntityMountEvent> {
    @Nonnull
    private final Lazy<CustomEntityType> passengerType;

    public DataEntityBeMounted(@Nonnull EntityMountEvent event) {
        super(event, event.getMount());
        passengerType = Lazy.create(() -> CustomEntityType.get(getPassenger()));
    }

    @Nonnull
    public Entity getPassenger() {
        return event.getEntity();
    }

    @Nonnull
    public CustomEntityType getPassengerType() {
        return passengerType.get();
    }
}
