package org.ricetea.barleyteaapi.api.block.feature.data;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockExplodeEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseBlockFeatureData;
import org.ricetea.barleyteaapi.api.block.CustomBlockType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataBlockBreakByBlockExplode extends BaseBlockFeatureData<BlockExplodeEvent> {
    @Nonnull
    private final Lazy<CustomBlockType> blockType;

    public DataBlockBreakByBlockExplode(@Nonnull BlockExplodeEvent event, @Nonnull Block block) {
        super(event, block);
        blockType = Lazy.create(() -> CustomBlockType.get(getBlockExploded()));
    }

    @Nonnull
    public Block getBlockExploded() {
        return Objects.requireNonNull(event.getBlock());
    }

    @Nonnull
    public CustomBlockType getBlockExplodedType() {
        return blockType.get();
    }
}
