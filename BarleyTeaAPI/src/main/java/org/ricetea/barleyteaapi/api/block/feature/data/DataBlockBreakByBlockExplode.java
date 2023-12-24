package org.ricetea.barleyteaapi.api.block.feature.data;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockExplodeEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseBlockFeatureData;
import org.ricetea.barleyteaapi.api.block.BaseBlock;
import org.ricetea.barleyteaapi.api.block.data.DataBlockType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataBlockBreakByBlockExplode extends BaseBlockFeatureData<BlockExplodeEvent> {
    @Nonnull
    private final Lazy<DataBlockType> blockType;

    public DataBlockBreakByBlockExplode(@Nonnull BlockExplodeEvent event, @Nonnull Block block) {
        super(event, block);
        blockType = Lazy.create(() -> BaseBlock.getBlockType(getBlockExploded()));
    }

    @Nonnull
    public Block getBlockExploded() {
        return Objects.requireNonNull(event.getBlock());
    }

    @Nonnull
    public DataBlockType getBlockExplodedType() {
        return blockType.get();
    }
}
