package org.ricetea.barleyteaapi.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.utils.CollectionUtil;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public final class ItemsUnregisteredEvent extends Event {

    private static final @Nonnull Lazy<HandlerList> lazyHandlerList = Lazy.create(HandlerList::new);

    private final @Nonnull List<CustomItem> items;

    public ItemsUnregisteredEvent(@Nonnull Collection<CustomItem> items) {
        this.items = CollectionUtil.toUnmodifiableList(items);
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return lazyHandlerList.get();
    }

    @Nonnull
    public List<CustomItem> getItems() {
        return items;
    }

    @Override
    @Nonnull
    public HandlerList getHandlers() {
        return lazyHandlerList.get();
    }
}
