package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.event.entity.CreatureSpawnEvent;

import javax.annotation.Nonnull;

public final class DataNaturalSpawnPosibility {
    @Nonnull
    private final Location location;

    @Nonnull
    private final CreatureSpawnEvent.SpawnReason reason;

    public DataNaturalSpawnPosibility(@Nonnull Location location, @Nonnull CreatureSpawnEvent.SpawnReason reason) {
        this.location = location;
        this.reason = reason;
    }

    @Nonnull
    public Location getLocation() {
        return location;
    }

    @Nonnull
    public World getWorld() {
        return location.getWorld();
    }

    @Nonnull
    public Biome getBiome() {
        return getWorld().getBiome(location);
    }

    @Nonnull
    public CreatureSpawnEvent.SpawnReason getSpawnReason() {
        return reason;
    }
}
