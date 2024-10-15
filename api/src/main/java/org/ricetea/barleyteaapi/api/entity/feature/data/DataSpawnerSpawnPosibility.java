package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.CreatureSpawner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class DataSpawnerSpawnPosibility {
    @Nonnull
    private final Location location;
    @Nullable
    private final CreatureSpawner spawner;

    public DataSpawnerSpawnPosibility(@Nonnull Location location, @Nullable CreatureSpawner spawner) {
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

    @Nullable
    public CreatureSpawner getSpawner() {
        return spawner;
    }
}
