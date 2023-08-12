package org.ricetea.barleyteaapi.internal.listener;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityDeath;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTick;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureKillEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDeath;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataKillEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataKillPlayer;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.internal.task.EntityTickTask;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class EntityDeathListener implements Listener {
    private static final Lazy<EntityDeathListener> inst = new Lazy<>(EntityDeathListener::new);

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
        EntityDamageEvent eventLastDamageCaused = entity.getLastDamageCause();
        EntityDamageByEntityEvent eventLastDamageCausedByEntity = ObjectUtil.tryCast(eventLastDamageCaused,
                EntityDamageByEntityEvent.class);
        NamespacedKey id = BaseEntity.getEntityID(entity);
        if (id != null) {
            BaseEntity entityType = EntityRegister.getInstance().lookupEntityType(id);
            if (entityType instanceof FeatureEntityDeath entityDeath) {
                boolean cancelled = !entityDeath
                        .handleEntityDeath(new DataEntityDeath(event, eventLastDamageCausedByEntity));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
            if (entityType instanceof FeatureEntityTick) {
                EntityTickTask.getInstance().removeEntity(entity);
            }
        }
        if (eventLastDamageCausedByEntity != null) {
            id = BaseEntity.getEntityID(eventLastDamageCausedByEntity.getDamager());
            if (id != null) {
                BaseEntity entityType = EntityRegister.getInstance().lookupEntityType(id);
                if (entityType instanceof FeatureKillEntity entityTypeEntityKill) {
                    boolean cancelled;
                    if (event instanceof PlayerDeathEvent playerDeathEvent) {
                        cancelled = !entityTypeEntityKill
                                .handleKillPlayer(new DataKillPlayer(playerDeathEvent,
                                        eventLastDamageCausedByEntity));
                    } else {
                        cancelled = !entityTypeEntityKill
                                .handleKillEntity(new DataKillEntity(event, eventLastDamageCausedByEntity));
                    }
                    if (cancelled) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
}
