package org.ricetea.barleyteaapi.api.block.feature.data;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataBlockEntityChange extends BaseFeatureData<EntityChangeBlockEvent> {
    @Nonnull
    private final Lazy<CustomEntityType> entityType;

    public DataBlockEntityChange(@Nonnull EntityChangeBlockEvent event) {
        super(event);
        entityType = Lazy.create(() -> CustomEntityType.get(getEntity()));
    }

    @Nonnull
    public Entity getEntity() {
        return Objects.requireNonNull(event.getEntity());
    }

    @Nonnull
    public CustomEntityType getEntityType() {
        return entityType.get();
    }

    @Nonnull
    public Block getBlock() {
        return Objects.requireNonNull(event.getBlock());
    }
}
