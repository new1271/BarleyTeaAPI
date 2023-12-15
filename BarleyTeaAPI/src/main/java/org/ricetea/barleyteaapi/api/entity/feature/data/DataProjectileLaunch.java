package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseProjectileFeatureData;

public final class DataProjectileLaunch extends BaseProjectileFeatureData<ProjectileLaunchEvent> {
    public DataProjectileLaunch(@Nonnull ProjectileLaunchEvent event) {
        super(event);
    }
}
