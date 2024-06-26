package org.ricetea.barleyteaapi.internal.listener.monitor;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityDamage;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityAttack;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByNothing;
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorEntityDamage;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityDamage;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDamagedByBlock;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDamagedByEntity;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDamagedByNothing;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.barleyteaapi.internal.linker.ItemFeatureLinker;
import org.ricetea.utils.Constants;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class EntityDamageMonitor implements Listener {

    private static final Lazy<EntityDamageMonitor> inst = Lazy.create(EntityDamageMonitor::new);

    private EntityDamageMonitor() {
    }

    @Nonnull
    public static EntityDamageMonitor getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void listenEntityDamage(EntityDamageEvent event) {
        if (event == null || event.isCancelled())
            return;
        if (event instanceof EntityDamageByEntityEvent _event) {
            onEntityDamageByEntity(_event);
        } else if (event instanceof EntityDamageByBlockEvent _event) {
            onEntityDamageByBlock(_event);
        } else {
            onEntityDamageByNothing(event);
        }
    }


    public void onEntityDamageByEntity(@Nonnull EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damagee = event.getEntity();
        EntityFeatureLinker.doFeature(damager, event, FeatureMonitorEntityDamage.class,
                FeatureMonitorEntityDamage::monitorEntityAttack, DataEntityAttack::new);
        EntityFeatureLinker.doFeature(damagee, event, FeatureMonitorEntityDamage.class,
                FeatureMonitorEntityDamage::monitorEntityDamagedByEntity, DataEntityDamagedByEntity::new);
    }

    public void onEntityDamageByBlock(@Nonnull EntityDamageByBlockEvent event) {
        Entity damagee = event.getEntity();
        EntityFeatureLinker.doFeature(damagee, event, FeatureMonitorEntityDamage.class,
                FeatureMonitorEntityDamage::monitorEntityDamagedByBlock, DataEntityDamagedByBlock::new);
    }

    public void onEntityDamageByNothing(@Nonnull EntityDamageEvent event) {
        Entity damagee = event.getEntity();
        EntityFeatureLinker.doFeature(damagee, event, FeatureMonitorEntityDamage.class,
                FeatureMonitorEntityDamage::monitorEntityDamagedByNothing, DataEntityDamagedByNothing::new);
    }
}
