package org.ricetea.barleyteaapi.api.block.feature;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;

public interface FeatureBlockLoad {
    void handleBlockLoaded(@Nonnull Block block);

    void handleBlockUnloaded(@Nonnull Block block);
}
