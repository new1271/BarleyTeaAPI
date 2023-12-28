package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;
import org.spigotmc.event.entity.EntityMountEvent;

import javax.annotation.Nonnull;

public final class DataEntityMount extends BaseEntityFeatureData<EntityMountEvent> {
    @Nonnull
    private final Lazy<CustomEntityType> mountType;

    public DataEntityMount(@Nonnull EntityMountEvent event) {
        super(event);
        mountType = Lazy.create(() -> CustomEntityType.get(getMount()));
    }

    @Nonnull
    public Entity getMount() {
        return event.getMount();
    }

    @Nonnull
    public CustomEntityType getMountType() {
        return mountType.get();
    }
}
