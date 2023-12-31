package org.ricetea.barleyteaapi.api.block.feature.data;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockExplodeEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseBlockFeatureData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public final class DataBlockExplode extends BaseBlockFeatureData<BlockExplodeEvent> {

    public DataBlockExplode(@Nonnull BlockExplodeEvent event) {
        super(event);
    }

    public @Nullable BlockState getExplodedBlockState() {
        return event.getExplodedBlockState();
    }

    public @Nonnull List<Block> blockList() {
        return Objects.requireNonNull(event.blockList());
    }

    public float getYield() {
        return event.getYield();
    }

    public void setYield(float yield) {
        event.setYield(yield);
    }
}
