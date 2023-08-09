package org.ricetea.barleyteaapi.api.entity.feature;

import org.bukkit.entity.Entity;

public interface FeatureChunkLoad {
    void handleChunkLoaded(Entity entity);

    void handleChunkUnloaded(Entity entity);
}
