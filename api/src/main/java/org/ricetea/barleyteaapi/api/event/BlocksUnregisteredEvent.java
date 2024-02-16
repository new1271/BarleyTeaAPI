package org.ricetea.barleyteaapi.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.utils.CollectionUtil;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class BlocksUnregisteredEvent extends Event {

    private static final @Nonnull Lazy<HandlerList> lazyHandlerList = Lazy.create(HandlerList::new);

    private final @Nonnull Collection<? extends CustomBlock> blocks;

    public BlocksUnregisteredEvent(@Nonnull Collection<? extends CustomBlock> blocks) {
        this.blocks = CollectionUtil.toUnmodifiableSet(blocks);
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return lazyHandlerList.get();
    }

    @Nonnull
    public Collection<? extends CustomBlock> getBlocks() {
        return blocks;
    }

    @Override
    @Nonnull
    public HandlerList getHandlers() {
        return lazyHandlerList.get();
    }
}
