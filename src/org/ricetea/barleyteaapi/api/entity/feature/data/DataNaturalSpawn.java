package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.ricetea.barleyteaapi.api.abstracts.DataEntityBase;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataNaturalSpawn extends DataEntityBase<CreatureSpawnEvent> {
    @Nonnull
    private final Lazy<DataEntityType> entityType;

    public DataNaturalSpawn(@Nonnull CreatureSpawnEvent event) {
        super(event);
        entityType = new Lazy<>(() -> BaseEntity.getEntityType(event.getEntity()));
    }

    @SuppressWarnings("null")
    @Nonnull
    public LivingEntity getEntity() {
        return event.getEntity();
    }

    @Nonnull
    public DataEntityType getEntityType() {
        return entityType.get();
    }

    @SuppressWarnings("null")
    @Nonnull
    public SpawnReason getSpawnReason() {
        return event.getSpawnReason();
    }

    @SuppressWarnings("null")
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
