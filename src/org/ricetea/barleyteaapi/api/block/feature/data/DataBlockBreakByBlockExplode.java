package org.ricetea.barleyteaapi.api.block.feature.data;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockExplodeEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseBlockFeatureData;
import org.ricetea.barleyteaapi.api.block.BaseBlock;
import org.ricetea.barleyteaapi.api.block.data.DataBlockType;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataBlockBreakByBlockExplode extends BaseBlockFeatureData<BlockExplodeEvent> {
    @Nonnull
    private final Lazy<DataBlockType> blockType;

    public DataBlockBreakByBlockExplode(@Nonnull BlockExplodeEvent event, @Nonnull Block block) {
        super(event, block);
        blockType = new Lazy<DataBlockType>(() -> BaseBlock.getBlockType(getBlockExploded()));
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
