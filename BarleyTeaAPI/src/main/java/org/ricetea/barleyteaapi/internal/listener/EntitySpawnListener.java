package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.NamespacedKey;
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
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.*;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityShoot;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileLaunch;
import org.ricetea.barleyteaapi.api.entity.feature.state.StateNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityShoot;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityShoot;
import org.ricetea.barleyteaapi.internal.helper.EntityFeatureHelper;
import org.ricetea.barleyteaapi.internal.helper.ItemFeatureHelper;
import org.ricetea.barleyteaapi.internal.task.EntityTickTask;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class EntitySpawnListener implements Listener {
    private static final Lazy<EntitySpawnListener> inst = Lazy.create(EntitySpawnListener::new);

    private EntitySpawnListener() {
    }

    @Nonnull
    public static EntitySpawnListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenEntitySpawn(EntitySpawnEvent event) {
        if (event == null || event.isCancelled())
            return;
        Entity entity = event.getEntity();
        if (event instanceof CreatureSpawnEvent creatureSpawnEvent) {
            onCreatureSpawn(creatureSpawnEvent);
        } else if (event instanceof ProjectileLaunchEvent projectileLaunchEvent) {
            onProjectileLaunch(projectileLaunchEvent);
        } else {
            return;
        }
        if (!event.isCancelled()) {
            NamespacedKey id = BaseEntity.getEntityID(entity);
            EntityRegister register = EntityRegister.getInstanceUnsafe();
            if (register != null && id != null) {
                BaseEntity baseEntity = register.lookup(id);
                if (baseEntity instanceof FeatureEntityTick) {
                    EntityTickTask.getInstance().addEntity(entity);
                }
            }
        }
    }

    private void onCreatureSpawn(@Nonnull CreatureSpawnEvent event) {
        SpawnReason reason = event.getSpawnReason();
        EntityRegister register = EntityRegister.getInstanceUnsafe();
        if (register == null || reason.equals(SpawnReason.CUSTOM) || reason.equals(SpawnReason.COMMAND) || reason.equals(SpawnReason.DEFAULT))
            return;
        Random rnd = ThreadLocalRandom.current();
        for (BaseEntity entityType : register.listAll(e -> e instanceof FeatureNaturalSpawn
                && e.getEntityTypeBasedOn().equals(event.getEntityType()))) {
            if (entityType != null) {
                FeatureNaturalSpawn spawnEntityType = (FeatureNaturalSpawn) entityType;
                double posibility = spawnEntityType.getSpawnPosibility(reason);
                if (posibility > 0 && (posibility >= 1 || rnd.nextDouble() < posibility)) {
                    StateNaturalSpawn result = spawnEntityType.handleNaturalSpawn(new DataNaturalSpawn(event));
                    switch (result) {
                        case Handled -> {
                            if (entityType instanceof FeatureEntityLoad feature) {
                                Entity entity = event.getEntity();
                                if (!entity.isDead())
                                    feature.handleEntityLoaded(entity);
                            }
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
    }


    private void onProjectileLaunch(@Nonnull ProjectileLaunchEvent event) {
        Projectile entity = event.getEntity();
        Entity shooter = EntityHelper.getProjectileShooterEntity(entity);
        if (!ItemFeatureHelper.forEachHandsCancellable(ObjectUtil.tryCast(shooter, LivingEntity.class), event,
                FeatureItemHoldEntityShoot.class, FeatureItemHoldEntityShoot::handleItemHoldEntityShoot,
                DataItemHoldEntityShoot::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureHelper.doFeatureCancellable(shooter, event, FeatureEntityShoot.class,
                FeatureEntityShoot::handleEntityShoot, DataEntityShoot::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureHelper.doFeatureCancellable(entity, event, FeatureProjectile.class,
                FeatureProjectile::handleProjectileLaunch, DataProjectileLaunch::new)) {
            event.setCancelled(true);
            return;
        }
    }
}
