package org.ricetea.barleyteaapi.api.entity.template;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureCommandSummon;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntitySpawn;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataCommandSummon;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.state.StateEntitySpawn;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.api.internal.misc.MiscInternalFunctions;

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
        MiscInternalFunctions functions = MiscInternalFunctions.getInstanceUnsafe();
        boolean result;
        if (functions == null) {
            result = EntityHelper.tryRegister(this, entity, this::handleEntitySpawn);
        } else {
            result = functions.tryRegisterEntityAfterSpawn(this, entity, this::handleEntitySpawn);
        }
        return result ? entity : null;
    }

    protected abstract boolean handleEntitySpawn(@Nonnull Entity entity);

    @Override
    public boolean handleCommandSummon(@Nonnull DataCommandSummon data) {
        return EntityHelper.tryRegister(this, data.getEntity(), this::handleEntitySpawn);
    }

    @Nonnull
    public StateEntitySpawn handleNaturalSpawn(@Nonnull DataNaturalSpawn data) {
        return EntityHelper.tryRegister(this, data.getEntity(), this::handleEntitySpawn)
                ? StateEntitySpawn.Handled : StateEntitySpawn.Skipped;
    }
}
