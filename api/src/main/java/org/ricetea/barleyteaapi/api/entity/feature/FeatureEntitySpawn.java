package org.ricetea.barleyteaapi.api.entity.feature;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface FeatureEntitySpawn extends EntityFeature {
    @Nullable
    Entity handleEntitySpawn(@Nonnull Location location);
}
