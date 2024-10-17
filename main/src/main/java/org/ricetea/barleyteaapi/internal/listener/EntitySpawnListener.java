package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityShoot;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureProjectile;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureSpawnerSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.data.*;
import org.ricetea.barleyteaapi.api.entity.feature.state.StateEntitySpawn;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.api.helper.FeatureHelper;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityShoot;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityShoot;
import org.ricetea.barleyteaapi.api.misc.RandomProvider;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.barleyteaapi.internal.linker.ItemFeatureLinker;
import org.ricetea.barleyteaapi.internal.listener.patch.EntitySpawnListenerPatch;
import org.ricetea.utils.Constants;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Singleton
@ApiStatus.Internal
public final class EntitySpawnListener implements Listener {
    private static final Lazy<EntitySpawnListener> inst = Lazy.create(EntitySpawnListener::new);
    private final List<EntitySpawnListenerPatch> patchList = new ArrayList<>();
    private final ReadWriteLock patchListLock = new ReentrantReadWriteLock();

    private EntitySpawnListener() {
    }

    @Nonnull
    public static EntitySpawnListener getInstance() {
        return inst.get();
    }

    public void addPatch(@Nonnull EntitySpawnListenerPatch patch) {
        Lock lock = patchListLock.writeLock();
        lock.lock();
        patchList.add(patch);
        lock.unlock();
    }

    public void removePatch(@Nonnull EntitySpawnListenerPatch patch) {
        Lock lock = patchListLock.writeLock();
        lock.lock();
        patchList.remove(patch);
        lock.unlock();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void listenEntitySpawn(EntitySpawnEvent event) {
        if (event == null || event.isCancelled())
            return;
        Entity entity = event.getEntity();
        if (event instanceof CreatureSpawnEvent creatureSpawnEvent) {
            onCreatureSpawn(creatureSpawnEvent);
        } else if (event instanceof SpawnerSpawnEvent spawnerSpawnEvent) {
            onSpawnerSpawn(spawnerSpawnEvent);
        } else if (event instanceof ProjectileLaunchEvent projectileLaunchEvent) {
            onProjectileLaunch(projectileLaunchEvent);
        } else {
            Lock lock = patchListLock.readLock();
            lock.lock();
            for (EntitySpawnListenerPatch patch : patchList) {
                if (patch.listenEntitySpawnInOtherCase(event))
                    break;
            }
            lock.unlock();
            return;
        }
        if (!event.isCancelled()) {
            EntityFeatureLinker.loadEntity(entity, false);
        }
    }

    private void onCreatureSpawn(@Nonnull CreatureSpawnEvent event) {
        SpawnReason reason = event.getSpawnReason();
        EntityRegister register = EntityRegister.getInstanceUnsafe();
        if (register == null || reason.equals(SpawnReason.CUSTOM) || reason.equals(SpawnReason.COMMAND) || reason.equals(SpawnReason.DEFAULT))
            return;
        RandomProvider rnd = RandomProvider.getInstance();
        Lazy<DataNaturalSpawnPosibility> dataLazy = Lazy.create(() ->
                new DataNaturalSpawnPosibility(event.getLocation(), event.getSpawnReason()));
        for (CustomEntity entityType : register.listAll(e -> e.getOriginalType().equals(event.getEntityType()))) {
            FeatureNaturalSpawn feature = FeatureHelper.getFeatureUnsafe(entityType, FeatureNaturalSpawn.class);
            if (feature == null)
                continue;
            double posibility = ObjectUtil.tryMap(() ->
                    feature.getNaturalSpawnPosibility(dataLazy.get()), 0.0);
            if (posibility > 0 && (posibility >= 1 || rnd.nextDouble() < posibility)) {
                StateEntitySpawn result = ObjectUtil.tryMap(() ->
                        feature.handleNaturalSpawn(new DataNaturalSpawn(event)), StateEntitySpawn.Skipped);
                if (event.isCancelled())
                    return;
                switch (result) {
                    case Handled -> {
                        return;
                    }
                    case Cancelled -> {
                        event.setCancelled(true);
                        return;
                    }
                    case Skipped -> {
                    }
                }
            }
        }
    }

    private void onSpawnerSpawn(@Nonnull SpawnerSpawnEvent event) {
        EntityRegister register = EntityRegister.getInstanceUnsafe();
        if (register == null)
            return;
        RandomProvider rnd = RandomProvider.getInstance();
        Lazy<DataSpawnerSpawnPosibility> dataLazy = Lazy.create(() ->
                new DataSpawnerSpawnPosibility(event.getLocation(), event.getSpawner()));
        for (CustomEntity entityType : register.listAll(e -> e.getOriginalType().equals(event.getEntityType()))) {
            FeatureSpawnerSpawn feature = FeatureHelper.getFeatureUnsafe(entityType, FeatureSpawnerSpawn.class);
            if (feature == null)
                continue;
            double posibility = ObjectUtil.tryMap(() ->
                    feature.getSpawnerSpawnPosibility(dataLazy.get()), 0.0);
            if (posibility > 0 && (posibility >= 1 || rnd.nextDouble() < posibility)) {
                StateEntitySpawn result = ObjectUtil.tryMap(() ->
                        feature.handleSpawnerSpawn(new DataSpawnerSpawn(event)), StateEntitySpawn.Skipped);
                if (event.isCancelled())
                    return;
                switch (result) {
                    case Handled -> {
                        return;
                    }
                    case Cancelled -> {
                        event.setCancelled(true);
                        return;
                    }
                    case Skipped -> {
                    }
                }
            }
        }
    }


    private void onProjectileLaunch(@Nonnull ProjectileLaunchEvent event) {
        Projectile entity = event.getEntity();
        Entity shooter = EntityHelper.getProjectileShooterEntity(entity);
        if (!ItemFeatureLinker.forEachEquipmentCancellable(ObjectUtil.tryCast(shooter, LivingEntity.class),
                event, Constants.HAND_SLOTS,
                FeatureItemHoldEntityShoot.class, FeatureItemHoldEntityShoot::handleItemHoldEntityShoot,
                DataItemHoldEntityShoot::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureLinker.doFeatureCancellable(shooter, event, FeatureEntityShoot.class,
                FeatureEntityShoot::handleEntityShoot, DataEntityShoot::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureLinker.doFeatureCancellable(entity, event, FeatureProjectile.class,
                FeatureProjectile::handleProjectileLaunch, DataProjectileLaunch::new)) {
            event.setCancelled(true);
            return;
        }
    }
}
