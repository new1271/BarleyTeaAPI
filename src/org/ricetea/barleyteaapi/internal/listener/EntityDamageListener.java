package org.ricetea.barleyteaapi.internal.listener;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.EntityDamagedByBlockData;
import org.ricetea.barleyteaapi.api.entity.feature.EntityDamagedByEntityData;
import org.ricetea.barleyteaapi.api.entity.feature.EntityDamagedByNothingData;
import org.ricetea.barleyteaapi.api.entity.feature.IEntityDamage;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.util.Lazy;

public final class EntityDamageListener implements Listener {

    private static final Lazy<EntityDamageListener> inst = new Lazy<>(EntityDamageListener::new);

    private EntityDamageListener() {
    }

    public static EntityDamageListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenEntityDamage(EntityDamageEvent event) {
        if (event == null || event.isCancelled())
            return;
        if (event instanceof EntityDamageByEntityEvent) {
            onEntityDamageByEntity((EntityDamageByEntityEvent) event);
        } else if (event instanceof EntityDamageByBlockEvent) {
            onEntityDamageByBlock((EntityDamageByBlockEvent) event);
        } else {
            onEntityDamageByNothing(event);
        }
    }

    public void onEntityDamageByEntity(@Nonnull EntityDamageByEntityEvent event) {
        NamespacedKey id = BaseEntity.getEntityID(event.getDamager());
        if (id != null) {
            BaseEntity entity = EntityRegister.getInstance().lookupEntityType(id);
            if (entity != null && entity instanceof IEntityDamage) {
                IEntityDamage entityDamage = (IEntityDamage) entity;
                boolean cancelled = entityDamage.handleEntityAttack(new EntityDamagedByEntityData(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        id = BaseEntity.getEntityID(event.getEntity());
        if (id != null) {
            BaseEntity entity = EntityRegister.getInstance().lookupEntityType(id);
            if (entity != null && entity instanceof IEntityDamage) {
                IEntityDamage entityDamage = (IEntityDamage) entity;
                boolean cancelled = entityDamage.handleEntityDamagedByEntity(new EntityDamagedByEntityData(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    public void onEntityDamageByBlock(@Nonnull EntityDamageByBlockEvent event) {
        NamespacedKey id = BaseEntity.getEntityID(event.getEntity());
        if (id != null) {
            BaseEntity entity = EntityRegister.getInstance().lookupEntityType(id);
            if (entity != null && entity instanceof IEntityDamage) {
                IEntityDamage entityDamage = (IEntityDamage) entity;
                boolean cancelled = entityDamage.handleEntityDamagedByBlock(new EntityDamagedByBlockData(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    public void onEntityDamageByNothing(@Nonnull EntityDamageEvent event) {
        NamespacedKey id = BaseEntity.getEntityID(event.getEntity());
        if (id != null) {
            BaseEntity entity = EntityRegister.getInstance().lookupEntityType(id);
            if (entity != null && entity instanceof IEntityDamage) {
                IEntityDamage entityDamage = (IEntityDamage) entity;
                boolean cancelled = entityDamage.handleEntityDamagedByNothing(new EntityDamagedByNothingData(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
