package org.ricetea.barleyteaapi.api.block.feature;

import org.bukkit.block.Block;

import javax.annotation.Nonnull;

public interface FeatureBlockTick {
    void handleTick(@Nonnull Block block);
}
