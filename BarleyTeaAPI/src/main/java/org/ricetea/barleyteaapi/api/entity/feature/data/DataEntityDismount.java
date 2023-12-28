package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;
import org.spigotmc.event.entity.EntityDismountEvent;

import javax.annotation.Nonnull;

public final class DataEntityDismount extends BaseEntityFeatureData<EntityDismountEvent> {
    @Nonnull
    private final Lazy<CustomEntityType> dismountedType;

    public DataEntityDismount(@Nonnull EntityDismountEvent event) {
        super(event);
        dismountedType = Lazy.create(() -> CustomEntityType.get(getDismounted()));
    }

    @Nonnull
    public Entity getDismounted() {
        return event.getDismounted();
    }

    @Nonnull
    public CustomEntityType getDismountedType() {
        return dismountedType.get();
    }
}
