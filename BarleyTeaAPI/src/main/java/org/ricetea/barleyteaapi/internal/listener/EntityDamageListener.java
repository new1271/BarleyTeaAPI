package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityDamage;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityAttack;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByNothing;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityDamage;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityAttack;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDamagedByBlock;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDamagedByEntity;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDamagedByNothing;
import org.ricetea.barleyteaapi.internal.helper.EntityFeatureHelper;
import org.ricetea.barleyteaapi.internal.helper.ItemFeatureHelper;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;

public final class EntityDamageListener implements Listener {

    private static final Lazy<EntityDamageListener> inst = Lazy.create(EntityDamageListener::new);

    private EntityDamageListener() {
    }

    @Nonnull
    public static EntityDamageListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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
        Entity damagee = event.getEntity();
        Entity damager = event.getDamager();
        if (!ItemFeatureHelper.forEachEquipmentCancellable(ObjectUtil.tryCast(damager, LivingEntity.class), event,
                FeatureItemHoldEntityDamage.class, FeatureItemHoldEntityDamage::handleItemHoldEntityAttack,
                DataItemHoldEntityAttack::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureHelper.doFeatureCancellable(damager, event, FeatureEntityDamage.class,
                FeatureEntityDamage::handleEntityAttack, DataEntityAttack::new)) {
            event.setCancelled(true);
            return;
        }
        if (!ItemFeatureHelper.forEachEquipmentCancellable(ObjectUtil.tryCast(damagee, LivingEntity.class), event,
                FeatureItemHoldEntityDamage.class, FeatureItemHoldEntityDamage::handleItemHoldEntityDamagedByEntity,
                DataItemHoldEntityDamagedByEntity::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureHelper.doFeatureCancellable(damagee, event, FeatureEntityDamage.class,
                FeatureEntityDamage::handleEntityDamagedByEntity, DataEntityDamagedByEntity::new)) {
            event.setCancelled(true);
        }
    }

    public void onEntityDamageByBlock(@Nonnull EntityDamageByBlockEvent event) {
        Entity damagee = event.getEntity();
        if (!ItemFeatureHelper.forEachEquipmentCancellable(ObjectUtil.tryCast(damagee, LivingEntity.class), event,
                FeatureItemHoldEntityDamage.class, FeatureItemHoldEntityDamage::handleItemHoldEntityDamagedByBlock,
                DataItemHoldEntityDamagedByBlock::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureHelper.doFeatureCancellable(damagee, event, FeatureEntityDamage.class,
                FeatureEntityDamage::handleEntityDamagedByBlock, DataEntityDamagedByBlock::new)) {
            event.setCancelled(true);
        }
    }

    public void onEntityDamageByNothing(@Nonnull EntityDamageEvent event) {
        Entity damagee = event.getEntity();
        if (!ItemFeatureHelper.forEachEquipmentCancellable(ObjectUtil.tryCast(damagee, LivingEntity.class), event,
                FeatureItemHoldEntityDamage.class, FeatureItemHoldEntityDamage::handleItemHoldEntityDamagedByNothing,
                DataItemHoldEntityDamagedByNothing::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureHelper.doFeatureCancellable(damagee, event, FeatureEntityDamage.class,
                FeatureEntityDamage::handleEntityDamagedByNothing, DataEntityDamagedByNothing::new)) {
            event.setCancelled(true);
        }
    }
}
