package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;

public interface FeatureEntityLoad {
    void handleEntityLoaded(@Nonnull Entity entity);

    void handleEntityUnloaded(@Nonnull Entity entity);
}
