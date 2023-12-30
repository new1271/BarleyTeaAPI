package org.ricetea.barleyteaapi.api.block.feature.data;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataBlockDropByEntity extends BaseFeatureData<EntityDropItemEvent> {

    @Nonnull
    private final Lazy<CustomEntityType> entityType;

    public DataBlockDropByEntity(@Nonnull EntityDropItemEvent event) {
        super(event);
        entityType = Lazy.create(() -> CustomEntityType.get(getEntity()));
    }

    public @Nonnull Entity getEntity() {
        return Objects.requireNonNull(event.getEntity());
    }

    public @Nonnull CustomEntityType getEntityType() {
        return entityType.get();
    }

    public @Nonnull Item getItem() {
        return Objects.requireNonNull(event.getItemDrop());
    }
}
