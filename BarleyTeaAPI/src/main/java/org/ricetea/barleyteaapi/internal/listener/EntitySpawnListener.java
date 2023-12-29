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
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.api.entity.feature.*;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityShoot;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataProjectileLaunch;
import org.ricetea.barleyteaapi.api.entity.feature.state.StateNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityShoot;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityShoot;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.barleyteaapi.internal.linker.ItemFeatureLinker;
import org.ricetea.barleyteaapi.internal.task.EntityTickTask;
import org.ricetea.utils.Constants;
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
            CustomEntity entityType = CustomEntity.get(entity);
            if (entityType instanceof FeatureEntityTick)
                EntityTickTask.getInstance().addEntity(entity);
        }
    }

    private void onCreatureSpawn(@Nonnull CreatureSpawnEvent event) {
        SpawnReason reason = event.getSpawnReason();
        EntityRegister register = EntityRegister.getInstanceUnsafe();
        if (register == null || reason.equals(SpawnReason.CUSTOM) || reason.equals(SpawnReason.COMMAND) || reason.equals(SpawnReason.DEFAULT))
            return;
        Random rnd = ThreadLocalRandom.current();
        for (CustomEntity entityType : register.listAll(e -> e instanceof FeatureNaturalSpawn
                && e.getOriginalType().equals(event.getEntityType()))) {
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
