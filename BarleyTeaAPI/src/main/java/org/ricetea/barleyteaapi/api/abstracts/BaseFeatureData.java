package org.ricetea.barleyteaapi.api.abstracts;

import org.bukkit.event.Event;

import javax.annotation.Nonnull;

public abstract class BaseFeatureData<T extends Event> {
    @Nonnull
    protected final T event;

    public BaseFeatureData(@Nonnull T event) {
        this.event = event;
    }

    @Nonnull
    public final T getBaseEvent() {
        return event;
    }

    public final boolean isAsync() {
        return event.isAsynchronous();
    }
}
