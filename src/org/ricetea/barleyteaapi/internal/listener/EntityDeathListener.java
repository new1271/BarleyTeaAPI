package org.ricetea.barleyteaapi.internal.listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureChunkLoad;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityDeath;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureKillEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDeath;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataKillEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataKillPlayer;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityDeath;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityKill;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDeath;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityKillEntity;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityKillPlayer;
import org.ricetea.barleyteaapi.internal.helper.EntityFeatureHelper;
import org.ricetea.barleyteaapi.internal.helper.ItemFeatureHelper;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

public final class EntityDeathListener implements Listener {
    private static final Lazy<EntityDeathListener> inst = Lazy.create(EntityDeathListener::new);

    private EntityDeathListener() {
    }

    @Nonnull
    public static EntityDeathListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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
        Entity damager = ObjectUtil.mapWhenNonnull(lastDamageEvent, EntityDamageByEntityEvent::getDamager);
        if (!ItemFeatureHelper.forEachEquipmentCancellable(ObjectUtil.tryCast(damager, LivingEntity.class), event,
                lastDamageEvent, FeatureItemHoldEntityKill.class,
                FeatureItemHoldEntityKill::handleItemHoldEntityKillEntity, DataItemHoldEntityKillEntity::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureHelper.doFeatureCancellable(damager, event, lastDamageEvent, FeatureKillEntity.class,
                FeatureKillEntity::handleKillEntity, DataKillEntity::new)) {
            event.setCancelled(true);
            return;
        }
        if (!ItemFeatureHelper.forEachEquipmentCancellable(ObjectUtil.tryCast(entity, LivingEntity.class), event,
                lastDamageEvent, FeatureItemHoldEntityDeath.class,
                FeatureItemHoldEntityDeath::handleItemHoldEntityDeath, DataItemHoldEntityDeath::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureHelper.doFeatureCancellable(entity, event, lastDamageEvent, FeatureEntityDeath.class,
                FeatureEntityDeath::handleEntityDeath, DataEntityDeath::new)) {
            event.setCancelled(true);
            return;
        }
        EntityFeatureHelper.doFeature(entity, FeatureChunkLoad.class, FeatureChunkLoad::handleChunkUnloaded);
    }

    private void onPlayerDeath(@Nonnull PlayerDeathEvent event, @Nullable EntityDamageByEntityEvent lastDamageEvent) {
        Entity damager = ObjectUtil.mapWhenNonnull(lastDamageEvent, EntityDamageByEntityEvent::getDamager);
        if (!ItemFeatureHelper.forEachEquipmentCancellable(ObjectUtil.tryCast(damager, LivingEntity.class), event,
                lastDamageEvent, FeatureItemHoldEntityKill.class,
                FeatureItemHoldEntityKill::handleItemHoldEntityKillPlayer, DataItemHoldEntityKillPlayer::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureHelper.doFeatureCancellable(damager, event, lastDamageEvent, FeatureKillEntity.class,
                FeatureKillEntity::handleKillPlayer, DataKillPlayer::new)) {
            event.setCancelled(true);
            return;
        }
    }
}
