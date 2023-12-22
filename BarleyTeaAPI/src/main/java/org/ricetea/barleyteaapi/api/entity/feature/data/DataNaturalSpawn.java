package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class DataNaturalSpawn extends BaseEntityFeatureData<CreatureSpawnEvent> {
    public DataNaturalSpawn(@Nonnull CreatureSpawnEvent event) {
        super(event);
    }

    @Nonnull
    public LivingEntity getEntity() {
        return event.getEntity();
    }

    @Nonnull
    public SpawnReason getSpawnReason() {
        return event.getSpawnReason();
    }

    @Nonnull
    public Location getLocation() {
        return event.getLocation();
    }

    public boolean isInSpecificWorld(@Nullable World world) {
        if (world == null)
            return false;
        World locWorld = event.getLocation().getWorld();
        if (locWorld.equals(world)) {
            return true;
        }
        NamespacedKey locWorldKey = locWorld.getKey();
        NamespacedKey tarWorldKey = world.getKey();
        return locWorldKey.equals(tarWorldKey);
    }
}
