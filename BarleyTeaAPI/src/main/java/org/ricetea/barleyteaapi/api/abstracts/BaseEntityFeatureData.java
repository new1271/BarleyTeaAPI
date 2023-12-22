package org.ricetea.barleyteaapi.api.abstracts;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityEvent;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class BaseEntityFeatureData<T extends EntityEvent> extends BaseFeatureData<T> {

    @Nonnull
    private final Entity entity;

    public BaseEntityFeatureData(@Nonnull T event) {
        this(event, event.getEntity());
    }

    public BaseEntityFeatureData(@Nonnull T event, @CheckForNull Entity entity) {
        super(event);
        this.entity = Objects.requireNonNull(entity);
    }

    @Nonnull
    public Entity getEntity() {
        return entity;
    }
}
