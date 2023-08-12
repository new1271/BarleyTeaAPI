package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;

public interface FeatureBarleyTeaAPILoad {
    void handleAPILoaded(@Nonnull Entity entity);

    void handleAPIUnloaded(@Nonnull Entity entity);
}
