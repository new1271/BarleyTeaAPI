package org.ricetea.barleyteaapi.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ricetea.barleyteaapi.api.block.BaseBlock;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.utils.CollectionUtil;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ItemsRegisteredEvent extends Event {

    private static final @Nonnull Lazy<HandlerList> lazyHandlerList = Lazy.create(HandlerList::new);

    private final @Nonnull List<BaseItem> items;

    public ItemsRegisteredEvent(@Nonnull Collection<BaseItem> items) {
        this.items = CollectionUtil.toUnmodifiableList(items);
    }

    @Nonnull
    public List<BaseItem> getItems() {
        return items;
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
