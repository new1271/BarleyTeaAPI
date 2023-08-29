package org.ricetea.barleyteaapi.api.block.feature;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;

public interface FeatureBarleyTeaAPILoad {
    void handleAPILoaded(@Nonnull Block block);

    void handleAPIUnloaded(@Nonnull Block block);
}
