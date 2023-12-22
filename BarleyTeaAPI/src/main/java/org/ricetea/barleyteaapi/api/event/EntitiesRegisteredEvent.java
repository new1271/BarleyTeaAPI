package org.ricetea.barleyteaapi.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.utils.CollectionUtil;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public final class EntitiesRegisteredEvent extends Event {

    private static final @Nonnull Lazy<HandlerList> lazyHandlerList = Lazy.create(HandlerList::new);

    private final @Nonnull List<BaseEntity> entities;

    public EntitiesRegisteredEvent(@Nonnull Collection<BaseEntity> entities) {
        this.entities = CollectionUtil.toUnmodifiableList(entities);
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return lazyHandlerList.get();
    }

    @Nonnull
    public List<BaseEntity> getEntities() {
        return entities;
    }

    @Override
    @Nonnull
    public HandlerList getHandlers() {
        return lazyHandlerList.get();
    }
}
