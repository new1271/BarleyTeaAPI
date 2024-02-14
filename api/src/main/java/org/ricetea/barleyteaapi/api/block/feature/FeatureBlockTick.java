package org.ricetea.barleyteaapi.api.block.feature;

import org.bukkit.block.Block;

import javax.annotation.Nonnull;

public interface FeatureBlockTick extends BlockFeature {
    void handleTick(@Nonnull Block block);
}
