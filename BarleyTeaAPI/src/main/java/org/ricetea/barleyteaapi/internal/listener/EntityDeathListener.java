package org.ricetea.barleyteaapi.internal.listener;

import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityDeath;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityLoad;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureKillEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDeath;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataKillEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataKillPlayer;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityDeath;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityKill;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDeath;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityKillEntity;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityKillPlayer;
import org.ricetea.barleyteaapi.internal.helper.EntityFeatureHelper;
import org.ricetea.barleyteaapi.internal.helper.ItemFeatureHelper;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
        Entity damager = ObjectUtil.safeMap(lastDamageEvent, EntityDamageByEntityEvent::getDamager);
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
        EntityFeatureHelper.doFeature(entity, FeatureEntityLoad.class, FeatureEntityLoad::handleEntityUnloaded);
    }

    private void onPlayerDeath(@Nonnull PlayerDeathEvent event, @Nullable EntityDamageByEntityEvent lastDamageEvent) {
        if (event.deathMessage() instanceof TranslatableComponent deathMessage && lastDamageEvent != null) {
            String key = deathMessage.key().toLowerCase();
            if (key.startsWith("death.attack.") && key.endsWith(".item")) {
                Entity damager = lastDamageEvent.getDamager();
                if (damager instanceof Projectile projectile) {
                    ProjectileSource source = projectile.getShooter();
                    if (source instanceof Entity sourceEntity) {
                        damager = sourceEntity;
                    }
                }
                if (damager instanceof LivingEntity livingDamager) {
                    EntityEquipment equipment = livingDamager.getEquipment();
                    if (equipment != null) {
                        ItemStack weapon = equipment.getItemInMainHand();
                        if (!weapon.getType().isAir()) {
                            BaseItem itemType = DataItemType.get(weapon).asCustomItem();
                            if (itemType != null) {
                                ItemMeta meta = weapon.getItemMeta();
                                if (meta != null && meta.displayName() instanceof TranslatableComponent displayName &&
                                        displayName.key().equalsIgnoreCase(itemType.getNameInTranslateKey())) {
                                    key = key.substring(0, key.length() - 5);
                                    event.deathMessage(deathMessage.key(key).args(deathMessage.args().subList(0, 2)));
                                }
                            }
                        }
                    }
                }
            }
        }

        Entity damager = ObjectUtil.safeMap(lastDamageEvent, EntityDamageByEntityEvent::getDamager);
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
