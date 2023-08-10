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
import org.ricetea.barleyteaapi.api.entity.feature.DataEntityDeath;
import org.ricetea.barleyteaapi.api.entity.feature.DataKillEntity;
import org.ricetea.barleyteaapi.api.entity.feature.DataKillPlayer;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityDeath;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureKillEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureKillPlayer;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.util.Lazy;

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
        EntityDamageByEntityEvent eventLastDamageCausedByEntity;
        if (eventLastDamageCaused == null) {
            eventLastDamageCausedByEntity = null;
        } else {
            if (eventLastDamageCaused instanceof EntityDamageByEntityEvent) {
                eventLastDamageCausedByEntity = (EntityDamageByEntityEvent) eventLastDamageCaused;
            } else {
                eventLastDamageCausedByEntity = null;
            }
        }
        NamespacedKey id = BaseEntity.getEntityID(entity);
        if (id != null) {
            BaseEntity entityType = EntityRegister.getInstance().lookupEntityType(id);
            if (entityType != null && entityType instanceof FeatureEntityDeath) {
                FeatureEntityDeath entityDeath = (FeatureEntityDeath) entityType;
                boolean cancelled = !entityDeath
                        .handleEntityDeath(new DataEntityDeath(event, eventLastDamageCausedByEntity));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if (eventLastDamageCausedByEntity != null) {
            id = BaseEntity.getEntityID(eventLastDamageCausedByEntity.getDamager());
            if (id != null) {
                BaseEntity entityType = EntityRegister.getInstance().lookupEntityType(id);
                if (entityType != null) {
                    boolean cancelled;
                    if (event instanceof PlayerDeathEvent) {
                        if (entityType instanceof FeatureKillPlayer) {
                            FeatureKillPlayer entityTypePlayerKill = (FeatureKillPlayer) entityType;
                            cancelled = !entityTypePlayerKill
                                    .handleKillPlayer(new DataKillPlayer((PlayerDeathEvent) event,
                                            eventLastDamageCausedByEntity));
                        } else {
                            cancelled = false;
                        }
                    } else {
                        if (entityType instanceof FeatureKillEntity) {
                            FeatureKillEntity entityTypeEntityKill = (FeatureKillEntity) entityType;
                            cancelled = !entityTypeEntityKill
                                    .handleKillEntity(new DataKillEntity(event, eventLastDamageCausedByEntity));
                        } else {
                            cancelled = false;
                        }
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
