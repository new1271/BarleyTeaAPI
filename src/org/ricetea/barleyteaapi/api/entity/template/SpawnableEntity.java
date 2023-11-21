package org.ricetea.barleyteaapi.api.entity.template;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureCommandSummon;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntitySpawn;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataCommandSummon;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.state.StateNaturalSpawn;

public abstract class SpawnableEntity extends BaseEntity
        implements FeatureCommandSummon, FeatureEntitySpawn {

    public SpawnableEntity(@Nonnull NamespacedKey key, @Nonnull EntityType entityTypeBasedOn) {
        super(key, entityTypeBasedOn);
    }

    @Nullable
    public Entity handleEntitySpawn(@Nonnull Location location) {
        World world = location.getWorld();
        if (world == null)
            return null;
        Entity entity = world.spawnEntity(location, getEntityTypeBasedOn(), SpawnReason.CUSTOM);
        if (entity == null)
            return null;
        if (tryRegister(entity, this::handleEntitySpawn)) {
            return entity;
        } else {
            entity.remove();
        }
        return null;
    }

    protected abstract boolean handleEntitySpawn(@Nonnull Entity entity);

    @Override
    public boolean handleCommandSummon(@Nonnull DataCommandSummon data) {
        return tryRegister(data.getEntity(), this::handleEntitySpawn);
    }

    @Nonnull
    public StateNaturalSpawn handleNaturalSpawn(@Nonnull DataNaturalSpawn data) {
        return tryRegister(data.getEntity(), this::handleEntitySpawn) ? StateNaturalSpawn.Handled
                : StateNaturalSpawn.Skipped;
    }
}
