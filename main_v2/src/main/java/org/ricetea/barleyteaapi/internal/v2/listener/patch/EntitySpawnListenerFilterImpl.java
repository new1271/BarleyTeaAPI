package org.ricetea.barleyteaapi.internal.v2.listener.patch;

import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.TrialSpawnerSpawnEvent;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureTrialSpawnerSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataTrialSpawnerSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataTrialSpawnerSpawnPosibility;
import org.ricetea.barleyteaapi.api.entity.feature.state.StateEntitySpawn;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.api.helper.FeatureHelper;
import org.ricetea.barleyteaapi.api.misc.RandomProvider;
import org.ricetea.barleyteaapi.internal.listener.filter.EntitySpawnListenerFilter;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;

public final class EntitySpawnListenerFilterImpl implements EntitySpawnListenerFilter {

    private static final EntitySpawnListenerFilterImpl _inst = new EntitySpawnListenerFilterImpl();

    @Nonnull
    public static EntitySpawnListenerFilterImpl getInstance() {
        return _inst;
    }

    private EntitySpawnListenerFilterImpl() {
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public boolean listenEntitySpawnFirst(@Nonnull EntitySpawnEvent event) {
        if (!(event instanceof TrialSpawnerSpawnEvent spawnEvent))
            return false;
        EntityRegister register = EntityRegister.getInstanceUnsafe();
        if (register == null)
            return true;
        RandomProvider rnd = RandomProvider.getInstance();
        Lazy<DataTrialSpawnerSpawnPosibility> dataLazy = Lazy.create(() ->
                new DataTrialSpawnerSpawnPosibility(spawnEvent.getLocation(), spawnEvent.getTrialSpawner()));
        for (CustomEntity entityType : register.listAll(e -> e.getOriginalType().equals(event.getEntityType()))) {
            FeatureTrialSpawnerSpawn feature = FeatureHelper.getFeatureUnsafe(entityType, FeatureTrialSpawnerSpawn.class);
            if (feature == null)
                continue;
            double posibility = ObjectUtil.tryMap(() ->
                    feature.getTrialSpawnerSpawnPosibility(dataLazy.get()), 0.0);
            if (posibility > 0 && (posibility >= 1 || rnd.nextDouble() < posibility)) {
                StateEntitySpawn result = ObjectUtil.tryMap(() ->
                        feature.handleTrialSpawnerSpawn(new DataTrialSpawnerSpawn(spawnEvent)), StateEntitySpawn.Skipped);
                if (event.isCancelled())
                    return true;
                switch (result) {
                    case Handled -> {
                        return true;
                    }
                    case Cancelled -> {
                        event.setCancelled(true);
                        return true;
                    }
                    case Skipped -> {
                    }
                }
            }
        }
        return true;
    }
}
