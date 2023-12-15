package org.ricetea.barleyteaapi.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.utils.CollectionUtil;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class EntitiesUnregisteredEvent extends Event {

    private static final @Nonnull Lazy<HandlerList> lazyHandlerList = Lazy.create(HandlerList::new);

    private final @Nonnull List<BaseEntity> entities;

    public EntitiesUnregisteredEvent(@Nonnull Collection<BaseEntity> entities) {
        this.entities = CollectionUtil.toUnmodifiableList(entities);
    }

    @Nonnull
    public List<BaseEntity> getEntities() {
        return entities;
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
