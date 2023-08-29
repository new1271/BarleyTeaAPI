package org.ricetea.barleyteaapi.api.block.feature;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;

public interface FeatureChunkLoad {
    void handleChunkLoaded(@Nonnull Block block);

    void handleChunkUnloaded(@Nonnull Block block);
}
