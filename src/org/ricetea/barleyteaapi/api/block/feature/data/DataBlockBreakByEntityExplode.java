package org.ricetea.barleyteaapi.api.block.feature.data;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataBlockBreakByEntityExplode extends BaseFeatureData<EntityExplodeEvent> {
    @Nonnull
    private final Lazy<DataEntityType> entityType;

    @Nonnull
    private final Block block;

    public DataBlockBreakByEntityExplode(@Nonnull EntityExplodeEvent event, @Nonnull Block block) {
        super(event);
        entityType = new Lazy<DataEntityType>(() -> BaseEntity.getEntityType(getEntityExploded()));
        this.block = block;
    }

    @Nonnull
    public Entity getEntityExploded() {
        return Objects.requireNonNull(event.getEntity());
    }

    @Nonnull
    public DataEntityType getEntityExplodedType() {
        return entityType.get();
    }

    @Nonnull
    public Block getBlock() {
        return block;
    }
}
