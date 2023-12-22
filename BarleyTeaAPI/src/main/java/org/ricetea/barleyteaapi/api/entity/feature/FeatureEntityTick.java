package org.ricetea.barleyteaapi.api.entity.feature;

import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;

public interface FeatureEntityTick {
    void handleTick(@Nonnull Entity entity);
}
