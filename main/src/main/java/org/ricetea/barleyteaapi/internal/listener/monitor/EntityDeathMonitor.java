package org.ricetea.barleyteaapi.internal.listener.monitor;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityDeath;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureKillEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDeath;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataKillEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataKillPlayer;
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorEntityDamage;
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorEntityDeath;
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorKillEntity;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityDeath;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityKill;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDeath;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityKillEntity;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityKillPlayer;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldPlayerDeath;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.barleyteaapi.internal.linker.ItemFeatureLinker;
import org.ricetea.utils.Constants;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class EntityDeathMonitor implements Listener {
    private static final Lazy<EntityDeathMonitor> inst = Lazy.create(EntityDeathMonitor::new);

    private EntityDeathMonitor() {
    }

    @Nonnull
    public static EntityDeathMonitor getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void listenEntityDeath(EntityDeathEvent event) {
        if (event == null || event.isCancelled())
            return;
        Entity entity = event.getEntity();
        if (event instanceof PlayerDeathEvent playerDeathEvent) {
            onPlayerDeath(playerDeathEvent,
                    ObjectUtil.tryCast(entity.getLastDamageCause(), EntityDamageByEntityEvent.class));
        } else {
            onEntityDeath(event,
                    ObjectUtil.tryCast(entity.getLastDamageCause(), EntityDamageByEntityEvent.class));
        }
    }

    private void onEntityDeath(@Nonnull EntityDeathEvent event, @Nullable EntityDamageByEntityEvent lastDamageEvent) {
        Entity entity = event.getEntity();
        Entity damager = ObjectUtil.safeMap(lastDamageEvent, EntityDamageByEntityEvent::getDamager);
        EntityFeatureLinker.doFeature(damager, event, lastDamageEvent, FeatureMonitorKillEntity.class,
                FeatureMonitorKillEntity::monitorKillEntity, DataKillEntity::new);
        EntityFeatureLinker.doFeature(entity, event, lastDamageEvent, FeatureMonitorEntityDeath.class,
                FeatureMonitorEntityDeath::monitorEntityDeath, DataEntityDeath::new);
        EntityFeatureLinker.unloadEntity(entity);
    }

    private void onPlayerDeath(@Nonnull PlayerDeathEvent event, @Nullable EntityDamageByEntityEvent lastDamageEvent) {
        Entity damager = ObjectUtil.safeMap(lastDamageEvent, EntityDamageByEntityEvent::getDamager);
        EntityFeatureLinker.doFeature(damager, event, lastDamageEvent, FeatureMonitorKillEntity.class,
                FeatureMonitorKillEntity::monitorKillPlayer, DataKillPlayer::new);
    }
}
