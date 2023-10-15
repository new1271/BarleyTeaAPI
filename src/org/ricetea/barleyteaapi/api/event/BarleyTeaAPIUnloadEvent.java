package org.ricetea.barleyteaapi.api.event;

import javax.annotation.Nonnull;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ricetea.utils.Lazy;

public final class BarleyTeaAPIUnloadEvent extends Event {

    static final Lazy<HandlerList> lazyHandlerList = Lazy.create(HandlerList::new);

    public BarleyTeaAPIUnloadEvent() {
    }

    @Override
    @Nonnull
    public final HandlerList getHandlers() {
        return lazyHandlerList.get();
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return lazyHandlerList.get();
    }
}
