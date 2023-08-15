package org.ricetea.barleyteaapi.internal.listener;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemClick;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemConsume;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemCustomDurability;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemDamage;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemFocus;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemBroken;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemClickBlock;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemClickEntity;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemClickNothing;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemConsume;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemDamage;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemGotFocus;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemLostFocus;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemMend;
import org.ricetea.barleyteaapi.api.item.feature.state.StateItemClickBlock;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.internal.helper.ItemFeatureHelper;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.ObjectUtil;

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
                if (baseItem instanceof FeatureItemDamage itemDamageFeature) {
                    if (!itemDamageFeature.handleItemDamage(new DataItemDamage(event))) {
                        event.setCancelled(true);
                        return;
                    }
                }
                if (baseItem instanceof FeatureItemCustomDurability customDurabilityFeature) {
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
                if (baseItem instanceof FeatureItemCustomDurability customDurabilityFeature) {
                    int damage = customDurabilityFeature.getDurabilityDamage(itemStack);
                    int repairAmount = event.getExperienceOrb().getExperience() * 2;
                    int newDamage = Math.max(damage - repairAmount, 0);
                    repairAmount = damage - newDamage;
                    if (baseItem instanceof FeatureItemDamage itemDamageFeature) {
                        event.setRepairAmount(repairAmount);
                        if (itemDamageFeature.handleItemMend(new DataItemMend(event))) {
                            if (event.isCancelled()) {
                                return;
                            }
                            int repairAmountTemp = event.getRepairAmount();
                            if (repairAmount != repairAmountTemp) {
                                newDamage = Math.max(damage - repairAmountTemp, 0);
                                repairAmountTemp = damage - newDamage;
                                repairAmount = repairAmountTemp;
                            }
                        } else {
                            event.setCancelled(true);
                            return;
                        }
                    }
                    event.setRepairAmount(repairAmount);
                    customDurabilityFeature.setDurabilityDamage(itemStack, newDamage);
                    if (itemStack.getItemMeta() instanceof Damageable damageable) {
                        int visualDamage = damageable.getDamage();
                        damageable.setDamage(repairAmount);
                        itemStack.setItemMeta(damageable);
                        Bukkit.getScheduler().runTask(BarleyTeaAPI.getInstance(), () -> {
                            Damageable _damageable = (Damageable) itemStack.getItemMeta();
                            _damageable.setDamage(visualDamage);
                            itemStack.setItemMeta(_damageable);
                        });
                    }
                } else {
                    if (baseItem instanceof FeatureItemDamage itemDamageFeature) {
                        if (!itemDamageFeature.handleItemMend(new DataItemMend(event))) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenPlayerSwitchItem(PlayerItemHeldEvent event) {
        if (event == null || event.isCancelled())
            return;
        PlayerInventory playerInventory = event.getPlayer().getInventory();
        if (!ItemFeatureHelper.doFeatureCancellable(playerInventory.getItem(event.getPreviousSlot()),
                event, FeatureItemFocus.class, FeatureItemFocus::handleItemLostFocus, DataItemLostFocus::new)) {
            event.setCancelled(true);
            return;
        }
        if (!ItemFeatureHelper.doFeatureCancellable(playerInventory.getItem(event.getNewSlot()),
                event, FeatureItemFocus.class, FeatureItemFocus::handleItemGotFocus, DataItemGotFocus::new)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemBreak(PlayerItemBreakEvent event) {
        if (event == null)
            return;
        ItemFeatureHelper.doFeature(event.getBrokenItem(), event, FeatureItemDamage.class,
                FeatureItemDamage::handleItemBroken, DataItemBroken::new);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemConsume(PlayerItemConsumeEvent event) {
        if (event == null || event.isCancelled())
            return;
        if (!ItemFeatureHelper.doFeatureCancellable(event.getItem(), event, FeatureItemConsume.class,
                FeatureItemConsume::handleItemConsume, DataItemConsume::new)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemInteractBlock(PlayerInteractEvent event) {
        if (event == null)
            return;
        Action action = event.getAction();
        if (action == null || action.equals(Action.PHYSICAL))
            return;
        boolean isNothing = event.getClickedBlock() == null || event.getClickedBlock().getType().isAir();
        if (isNothing) {
            if (!ItemFeatureHelper.doFeatureAndReturn(event.getItem(), event, FeatureItemClick.class,
                    FeatureItemClick::handleItemClickNothing, DataItemClickNothing::new, true)) {
                event.setUseInteractedBlock(Result.DENY);
                event.setUseItemInHand(Result.DENY);
            }
        } else {
            switch (ItemFeatureHelper.doFeatureAndReturn(event.getItem(), event, FeatureItemClick.class,
                    FeatureItemClick::handleItemClickBlock, DataItemClickBlock::new, StateItemClickBlock.Skipped)) {
                case Cancelled:
                    event.setUseInteractedBlock(Result.DENY);
                    event.setUseItemInHand(Result.DENY);
                    break;
                case UseItem:
                    event.setUseInteractedBlock(Result.DENY);
                    event.setUseItemInHand(Result.ALLOW);
                    break;
                case UseBlock:
                    event.setUseInteractedBlock(Result.ALLOW);
                    event.setUseItemInHand(Result.DENY);
                    break;
                case Skipped:
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemInteractEntity(PlayerInteractEntityEvent event) {
        if (event == null || event.isCancelled())
            return;
        ItemStack itemStack = event.getPlayer().getInventory().getItem(ObjectUtil.throwWhenNull(event.getHand()));
        if (!ItemFeatureHelper.doFeatureCancellable(itemStack, event, FeatureItemClick.class,
                FeatureItemClick::handleItemClickEntity, DataItemClickEntity::new)) {
            event.setCancelled(true);
            return;
        }
    }
}
