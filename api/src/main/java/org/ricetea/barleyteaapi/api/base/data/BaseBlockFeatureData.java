package org.ricetea.barleyteaapi.api.base.data;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockEvent;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class BaseBlockFeatureData<T extends BlockEvent> extends BaseFeatureData<T> {

    @Nonnull
    private final Block block;

    public BaseBlockFeatureData(@Nonnull T event) {
        super(event);
        this.block = Objects.requireNonNull(event.getBlock());
    }

    public BaseBlockFeatureData(@Nonnull T event, @Nonnull Block block) {
        super(event);
        this.block = block;
    }

    @Nonnull
    public Block getBlock() {
        return block;
    }
}
