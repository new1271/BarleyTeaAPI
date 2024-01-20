package org.ricetea.barleyteaapi.api.entity.template;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureCommandSummon;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityLoad;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntitySpawn;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTick;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataCommandSummon;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.state.StateNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.internal.task.EntityTickTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class SpawnableEntity extends DefaultEntity
        implements FeatureCommandSummon, FeatureEntitySpawn {

    public SpawnableEntity(@Nonnull NamespacedKey key, @Nonnull EntityType originalType) {
        super(key, originalType);
    }

    @Nullable
    public Entity handleEntitySpawn(@Nonnull Location location) {
        World world = location.getWorld();
        if (world == null)
            return null;
        Class<? extends Entity> entityClazz = getOriginalType().getEntityClass();
        if (entityClazz == null)
            return null;
        Entity entity = world.spawn(location, entityClazz, false, null);
        if (EntityHelper.tryRegister(this, entity, this::handleEntitySpawn)) {
            if (this instanceof FeatureEntityLoad feature) {
                feature.handleEntityLoaded(entity);
            }
            if (this instanceof FeatureEntityTick) {
                EntityTickTask.getInstance().addEntity(entity);
            }
            return entity;
        } else {
            entity.remove();
        }
        return null;
    }

    protected abstract boolean handleEntitySpawn(@Nonnull Entity entity);

    @Override
    public boolean handleCommandSummon(@Nonnull DataCommandSummon data) {
        return EntityHelper.tryRegister(this, data.getEntity(), this::handleEntitySpawn);
    }

    @Nonnull
    public StateNaturalSpawn handleNaturalSpawn(@Nonnull DataNaturalSpawn data) {
        return EntityHelper.tryRegister(this, data.getEntity(), this::handleEntitySpawn)
                ? StateNaturalSpawn.Handled : StateNaturalSpawn.Skipped;
    }
}
