package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.TrialSpawner;

import javax.annotation.Nonnull;

public final class DataTrialSpawnerSpawnPosibility {
    @Nonnull
    private final Location location;
    @Nonnull
    private final TrialSpawner spawner;

    public DataTrialSpawnerSpawnPosibility(@Nonnull Location location, @Nonnull TrialSpawner spawner) {
        this.location = location;
        this.spawner = spawner;
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
    public TrialSpawner getSpawner() {
        return spawner;
    }
}
