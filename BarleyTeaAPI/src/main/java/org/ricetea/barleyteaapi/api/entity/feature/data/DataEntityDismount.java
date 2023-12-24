package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.utils.Lazy;
import org.spigotmc.event.entity.EntityDismountEvent;

import javax.annotation.Nonnull;

public final class DataEntityDismount extends BaseEntityFeatureData<EntityDismountEvent> {
    @Nonnull
    private final Lazy<DataEntityType> dismountedType;

    public DataEntityDismount(@Nonnull EntityDismountEvent event) {
        super(event);
        dismountedType = Lazy.create(() -> BaseEntity.getEntityType(getDismounted()));
    }

    @Nonnull
    public Entity getDismounted() {
        return event.getDismounted();
    }

    @Nonnull
    public DataEntityType getDismountedType() {
        return dismountedType.get();
    }
}
