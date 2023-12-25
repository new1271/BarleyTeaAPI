package org.ricetea.barleyteaapi.api.block.feature.data;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataBlockBreakByEntityExplode extends BaseFeatureData<EntityExplodeEvent> {
    @Nonnull
    private final Lazy<CustomEntityType> entityType;

    @Nonnull
    private final Block block;

    public DataBlockBreakByEntityExplode(@Nonnull EntityExplodeEvent event, @Nonnull Block block) {
        super(event);
        entityType = Lazy.create(() -> CustomEntityType.get(getEntityExploded()));
        this.block = block;
    }

    @Nonnull
    public Entity getEntityExploded() {
        return Objects.requireNonNull(event.getEntity());
    }

    @Nonnull
    public CustomEntityType getEntityExplodedType() {
        return entityType.get();
    }

    @Nonnull
    public Block getBlock() {
        return block;
    }
}
