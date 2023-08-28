package org.ricetea.barleyteaapi.api.abstracts;

import java.util.Objects;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityEvent;

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
