package org.ricetea.barleyteaapi.api.block.feature.data;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataBlockDropByEntity extends BaseFeatureData<EntityDropItemEvent> {

    @Nonnull
    private final Lazy<DataEntityType> entityType;

    public DataBlockDropByEntity(@Nonnull EntityDropItemEvent event) {
        super(event);
        entityType = Lazy.create(() -> BaseEntity.getEntityType(getEntity()));
    }

    public @Nonnull Entity getEntity() {
        return Objects.requireNonNull(event.getEntity());
    }

    public @Nonnull DataEntityType getEntityType() {
        return entityType.get();
    }

    public @Nonnull Item getItem() {
        return Objects.requireNonNull(event.getItemDrop());
    }
}
