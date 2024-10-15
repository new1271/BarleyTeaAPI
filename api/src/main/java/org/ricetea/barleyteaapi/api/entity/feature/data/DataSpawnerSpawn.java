package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class DataSpawnerSpawn extends BaseEntityFeatureData<SpawnerSpawnEvent> {
    public DataSpawnerSpawn(@Nonnull SpawnerSpawnEvent event) {
        super(event);
    }

    @Nonnull
    public Entity getEntity() {
        return event.getEntity();
    }

    @Nullable
    public CreatureSpawner getSpawner() {
        return event.getSpawner();
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
