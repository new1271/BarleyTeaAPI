package org.ricetea.barleyteaapi.api.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureCommandSummon;

public abstract class SpawnableEntity extends BaseEntity
        implements FeatureCommandSummon {

    public SpawnableEntity(@Nonnull NamespacedKey key, @Nonnull EntityType entityTypeBasedOn) {
        super(key, entityTypeBasedOn);
    }

    @Nullable
    public Entity spawn(@Nullable Location location) {
        if (location == null)
            return null;
        World world = location.getWorld();
        if (world == null)
            return null;
        Entity entity = world.spawnEntity(location, getEntityTypeBasedOn(), SpawnReason.CUSTOM);
        if (entity == null)
            return null;
        spawn(entity);
        return entity;
    }

    protected abstract void spawn(@Nonnull Entity entity);

    @Override
    public boolean handleCommandSummon(@Nonnull Entity entity, @Nullable String nbt) {
        spawn(entity);
        return true;
    }
}
