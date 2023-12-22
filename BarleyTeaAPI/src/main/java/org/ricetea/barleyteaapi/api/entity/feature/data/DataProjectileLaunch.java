package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseProjectileFeatureData;

import javax.annotation.Nonnull;

public final class DataProjectileLaunch extends BaseProjectileFeatureData<ProjectileLaunchEvent> {
    public DataProjectileLaunch(@Nonnull ProjectileLaunchEvent event) {
        super(event);
    }
}
