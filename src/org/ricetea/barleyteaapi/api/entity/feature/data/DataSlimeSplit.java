package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.ricetea.barleyteaapi.api.abstracts.DataEntityBase;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataSlimeSplit extends DataEntityBase<SlimeSplitEvent> {

    @Nonnull
    private final Lazy<DataEntityType> entityType;

    @SuppressWarnings("null")
    public DataSlimeSplit(@Nonnull SlimeSplitEvent event) {
        super(event);
        entityType = new Lazy<>(() -> BaseEntity.getEntityType(event.getEntity()));
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

    public int getCount() {
        return event.getCount();
    }

    public void setCount(int count) {
        event.setCount(count);
    }
}
