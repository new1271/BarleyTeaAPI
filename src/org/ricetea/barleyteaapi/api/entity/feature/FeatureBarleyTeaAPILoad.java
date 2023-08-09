package org.ricetea.barleyteaapi.api.entity.feature;

import org.bukkit.entity.Entity;

public interface FeatureBarleyTeaAPILoad {
    void handleAPILoaded(Entity entity);

    void handleAPIUnloaded(Entity entity);
}
