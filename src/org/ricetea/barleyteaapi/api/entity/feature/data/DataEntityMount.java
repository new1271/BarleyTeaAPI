package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;
import org.spigotmc.event.entity.EntityMountEvent;

public final class DataEntityMount extends BaseEntityFeatureData<EntityMountEvent> {
    @Nonnull
    private final Lazy<DataEntityType> mountType;

    public DataEntityMount(@Nonnull EntityMountEvent event) {
        super(event);
        mountType = new Lazy<>(() -> BaseEntity.getEntityType(getMount()));
    }

    @SuppressWarnings("null")
    @Nonnull
    public Entity getMount() {
        return event.getMount();
    }

    @Nonnull
    public DataEntityType getMountType() {
        return mountType.get();
    }
}
