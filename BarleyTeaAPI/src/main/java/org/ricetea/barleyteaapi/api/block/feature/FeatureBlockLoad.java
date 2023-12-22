package org.ricetea.barleyteaapi.api.block.feature;

import org.bukkit.block.Block;

import javax.annotation.Nonnull;

public interface FeatureBlockLoad {
    void handleBlockLoaded(@Nonnull Block block);

    void handleBlockUnloaded(@Nonnull Block block);
}
