package org.ricetea.barleyteaapi.api.block.feature.data;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataBlockEntityChange extends BaseFeatureData<EntityChangeBlockEvent> {
    @Nonnull
    private final Lazy<DataEntityType> entityType;

    public DataBlockEntityChange(@Nonnull EntityChangeBlockEvent event) {
        super(event);
        entityType = new Lazy<DataEntityType>(() -> BaseEntity.getEntityType(getEntity()));
    }

    @Nonnull
    public Entity getEntity() {
        return Objects.requireNonNull(event.getEntity());
    }

    @Nonnull
    public DataEntityType getEntityType() {
        return entityType.get();
    }

    @Nonnull
    public Block getBlock() {
        return Objects.requireNonNull(event.getBlock());
    }
}
