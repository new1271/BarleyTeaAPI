package org.ricetea.barleyteaapi.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.utils.CollectionUtil;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public final class BlocksRegisteredEvent extends Event {

    private static final @Nonnull Lazy<HandlerList> lazyHandlerList = Lazy.create(HandlerList::new);

    private final @Nonnull List<CustomBlock> blocks;

    public BlocksRegisteredEvent(@Nonnull Collection<CustomBlock> blocks) {
        this.blocks = CollectionUtil.toUnmodifiableList(blocks);
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return lazyHandlerList.get();
    }

    @Nonnull
    public List<CustomBlock> getBlocks() {
        return blocks;
    }

    @Override
    @Nonnull
    public HandlerList getHandlers() {
        return lazyHandlerList.get();
    }
}
