package org.ricetea.barleyteaapi.api.block.feature;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;

public interface FeatureBlockTick {
    void handleTick(@Nonnull Block block);
}
