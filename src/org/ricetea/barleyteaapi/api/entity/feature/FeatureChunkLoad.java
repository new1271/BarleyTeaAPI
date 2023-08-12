package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;

public interface FeatureChunkLoad {
    void handleChunkLoaded(@Nonnull Entity entity);

    void handleChunkUnloaded(@Nonnull Entity entity);
}
