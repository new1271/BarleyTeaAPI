package org.ricetea.barleyteaapi.api.entity.feature;

import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;

public interface FeatureEntityTick extends EntityFeature {
    void handleTick(@Nonnull Entity entity);
}
