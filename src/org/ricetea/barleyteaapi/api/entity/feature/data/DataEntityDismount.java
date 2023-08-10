package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.api.abstracts.DataEntityBase;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;
import org.spigotmc.event.entity.EntityDismountEvent;

public final class DataEntityDismount extends DataEntityBase<EntityDismountEvent> {
    @Nonnull
    private final Lazy<DataEntityType> entityType, dismountedType;

    public DataEntityDismount(@Nonnull EntityDismountEvent event) {
        super(event);
        entityType = new Lazy<>(() -> BaseEntity.getEntityType(event.getEntity()));
        dismountedType = new Lazy<>(() -> BaseEntity.getEntityType(event.getDismounted()));
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
    public Entity getDismounted() {
        return event.getDismounted();
    }

    @Nonnull
    public DataEntityType getDismountedType() {
        return dismountedType.get();
    }
}
