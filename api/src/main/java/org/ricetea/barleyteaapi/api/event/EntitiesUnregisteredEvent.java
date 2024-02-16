package org.ricetea.barleyteaapi.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.utils.CollectionUtil;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class EntitiesUnregisteredEvent extends Event {

    private static final @Nonnull Lazy<HandlerList> lazyHandlerList = Lazy.create(HandlerList::new);

    private final @Nonnull Collection<? extends CustomEntity> entities;

    public EntitiesUnregisteredEvent(@Nonnull Collection<? extends CustomEntity> entities) {
        this.entities = CollectionUtil.toUnmodifiableSet(entities);
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return lazyHandlerList.get();
    }

    @Nonnull
    public Collection<? extends CustomEntity> getEntities() {
        return entities;
    }

    @Override
    @Nonnull
    public HandlerList getHandlers() {
        return lazyHandlerList.get();
    }
}
