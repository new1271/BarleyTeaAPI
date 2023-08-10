package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.api.abstracts.DataEntityBase;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;
import org.spigotmc.event.entity.EntityMountEvent;

public final class DataEntityMount extends DataEntityBase<EntityMountEvent> {
    @Nonnull
    private final Lazy<DataEntityType> entityType, mountType;

    public DataEntityMount(@Nonnull EntityMountEvent event) {
        super(event);
        entityType = new Lazy<>(() -> BaseEntity.getEntityType(event.getEntity()));
        mountType = new Lazy<>(() -> BaseEntity.getEntityType(event.getMount()));
    }

    @SuppressWarnings("null")
    @Nonnull
    public Entity getEntity() {
        return event.getEntity();
    }

    @Nonnull
    public DataEntityType getEntityType() {
        return entityType.get();
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
