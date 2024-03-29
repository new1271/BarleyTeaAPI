package org.ricetea.barleyteaapi.api.entity.feature;

import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;

public interface FeatureEntityLoad extends EntityFeature {
    void handleEntityLoaded(@Nonnull Entity entity);

    void handleEntityUnloaded(@Nonnull Entity entity);
}
