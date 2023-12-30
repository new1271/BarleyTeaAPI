package org.ricetea.barleyteaapi.api.block.feature.data;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockFromToEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseBlockFeatureData;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataBlockMove extends BaseBlockFeatureData<BlockFromToEvent> {

    public DataBlockMove(@Nonnull BlockFromToEvent event) {
        super(event);
    }

    public @Nonnull BlockFace getFace() {
        return Objects.requireNonNull(event.getFace());
    }

    public @Nonnull Block getToBlock() {
        return Objects.requireNonNull(event.getToBlock());
    }
}
