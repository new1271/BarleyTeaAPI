package org.ricetea.barleyteaapi.internal.listener;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureCustomDurability;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.util.Lazy;

public final class PlayerEventListener implements Listener {
    private static final Lazy<PlayerEventListener> inst = new Lazy<>(PlayerEventListener::new);

    private PlayerEventListener() {
    }

    @Nonnull
    public static PlayerEventListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemDamaged(PlayerItemDamageEvent event) {
        if (event == null || event.isCancelled())
            return;
        ItemStack itemStack = event.getItem();
        if (itemStack != null) {
            NamespacedKey id = BaseItem.getItemID(itemStack);
            if (id != null) {
                BaseItem baseItem = ItemRegister.getInstance().lookupItemType(id);
                if (baseItem instanceof FeatureCustomDurability customDurabilityFeature) {
                    int newDamage = customDurabilityFeature.getDurabilityDamage(itemStack) + event.getDamage();
                    if (newDamage < customDurabilityFeature.getMaxDurability(itemStack)) {
                        customDurabilityFeature.setDurabilityDamage(itemStack, newDamage);
                        event.setDamage(0);
                    } else {
                        event.setDamage(itemStack.getType().getMaxDurability()
                                - ((Damageable) itemStack.getItemMeta()).getDamage());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemMending(PlayerItemMendEvent event) {
        if (event == null || event.isCancelled())
            return;
        ItemStack itemStack = event.getItem();
        if (itemStack != null) {
            NamespacedKey id = BaseItem.getItemID(itemStack);
            if (id != null) {
                BaseItem baseItem = ItemRegister.getInstance().lookupItemType(id);
                if (baseItem instanceof FeatureCustomDurability customDurabilityFeature) {
                    int damage = customDurabilityFeature.getDurabilityDamage(itemStack);
                    int repairAmount = event.getExperienceOrb().getExperience() * 2;
                    int newDamage = Math.max(damage - repairAmount, 0);
                    if (itemStack.getItemMeta() instanceof Damageable damageable) {
                        customDurabilityFeature.setDurabilityDamage(itemStack, newDamage);
                        damageable = (Damageable) itemStack.getItemMeta();
                        int visualDamage = damageable.getDamage();
                        damageable.setDamage(damage - newDamage);
                        event.setRepairAmount(damage - newDamage);
                        itemStack.setItemMeta(damageable);
                        Bukkit.getScheduler().runTask(BarleyTeaAPI.getInstance(), () -> {
                            Damageable _damageable = (Damageable) itemStack.getItemMeta();
                            _damageable.setDamage(visualDamage);
                            itemStack.setItemMeta(_damageable);
                        });
                    }
                }
            }
        }
    }
}
