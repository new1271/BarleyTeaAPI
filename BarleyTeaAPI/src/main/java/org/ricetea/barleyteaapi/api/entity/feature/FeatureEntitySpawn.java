package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface FeatureEntitySpawn {
    @Nullable
    Entity handleEntitySpawn(@Nonnull Location location);
}
