package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface FeatureEntitySpawn {
    Entity handleEntitySpawn(@Nonnull Location location);
}
