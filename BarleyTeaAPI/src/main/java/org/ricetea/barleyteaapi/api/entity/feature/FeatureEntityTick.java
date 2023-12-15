package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;

public interface FeatureEntityTick {
    void handleTick(@Nonnull Entity entity);
}
