package org.ricetea.barleyteaapi.internal.listener;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockClick;
import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockClicked;
import org.ricetea.barleyteaapi.api.block.feature.state.StateBlockClicked;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.feature.*;
import org.ricetea.barleyteaapi.api.item.feature.data.*;
import org.ricetea.barleyteaapi.api.item.feature.state.StateItemClickBlock;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.internal.linker.BlockFeatureLinker;
import org.ricetea.barleyteaapi.internal.linker.ItemFeatureLinker;
import org.ricetea.utils.Constants;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.util.Objects;

@Singleton
@ApiStatus.Internal
public final class PlayerEventListener implements Listener {
    private static final Lazy<PlayerEventListener> inst = Lazy.create(PlayerEventListener::new);

    private PlayerEventListener() {
    }

    @Nonnull
    public static PlayerEventListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemDamaged(PlayerItemDamageEvent event) {
        if (event == null || event.isCancelled() || !ItemRegister.hasRegistered())
            return;
        ItemStack itemStack = event.getItem();
        CustomItem itemType = CustomItem.get(itemStack);
        if (itemType == null)
            return;
        if (itemType instanceof FeatureItemDamage feature) {
            try {
                if (!feature.handleItemDamage(new DataItemDamage(event))) {
                    event.setCancelled(true);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (itemType instanceof FeatureItemCustomDurability feature) {
            try {
                int newDamage = feature.getDurabilityDamage(itemStack) + event.getDamage();
                if (newDamage < feature.getMaxDurability(itemStack)) {
                    feature.setDurabilityDamage(itemStack, newDamage);
                    event.setDamage(0);
                } else {
                    event.setDamage(itemStack.getType().getMaxDurability()
                            - ((Damageable) itemStack.getItemMeta()).getDamage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemMending(PlayerItemMendEvent event) {
        if (event == null || event.isCancelled() || !ItemRegister.hasRegistered())
            return;
        ItemStack itemStack = event.getItem();
        CustomItem itemType = CustomItem.get(itemStack);
        if (itemType == null)
            return;
        if (itemType instanceof FeatureItemCustomDurability feature) {
            int repairAmount = event.getExperienceOrb().getExperience() * 2;
            try {
                int damage = feature.getDurabilityDamage(itemStack);
                int newDamage = Math.max(damage - repairAmount, 0);
                repairAmount = damage - newDamage;
                if (itemType instanceof FeatureItemDamage feature2) {
                    event.setRepairAmount(repairAmount);
                    if (feature2.handleItemMend(new DataItemMend(event))) {
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
                feature.setDurabilityDamage(itemStack, newDamage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (itemStack.getItemMeta() instanceof Damageable damageable) {
                int visualDamage = damageable.getDamage();
                damageable.setDamage(repairAmount);
                itemStack.setItemMeta(damageable);
                Bukkit.getScheduler().scheduleSyncDelayedTask(BarleyTeaAPI.getInstance(),
                        () -> {
                            Damageable _damageable = (Damageable) itemStack.getItemMeta();
                            _damageable.setDamage(visualDamage);
                            itemStack.setItemMeta(_damageable);
                        });
            }
        } else {
            if (itemType instanceof FeatureItemDamage feature) {
                try {
                    if (!feature.handleItemMend(new DataItemMend(event))) {
                        event.setCancelled(true);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenPlayerSwitchItem(PlayerItemHeldEvent event) {
        if (event == null || event.isCancelled())
            return;
        PlayerInventory playerInventory = event.getPlayer().getInventory();
        if (!ItemFeatureLinker.doFeatureCancellable(playerInventory.getItem(event.getPreviousSlot()),
                event, FeatureItemFocus.class, FeatureItemFocus::handleItemLostFocus, DataItemLostFocus::new)) {
            event.setCancelled(true);
            return;
        }
        if (!ItemFeatureLinker.doFeatureCancellable(playerInventory.getItem(event.getNewSlot()),
                event, FeatureItemFocus.class, FeatureItemFocus::handleItemGotFocus, DataItemGotFocus::new)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemBreak(PlayerItemBreakEvent event) {
        if (event == null)
            return;
        ItemFeatureLinker.doFeature(event.getBrokenItem(), event, FeatureItemDamage.class,
                FeatureItemDamage::handleItemBroken, DataItemBroken::new);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemConsume(PlayerItemConsumeEvent event) {
        if (event == null || event.isCancelled())
            return;
        if (!ItemFeatureLinker.doFeatureCancellable(event.getItem(), event, FeatureItemConsume.class,
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
        if (action.equals(Action.PHYSICAL))
            return;
        Block clickedBlock = event.getClickedBlock();
        boolean isNothing = clickedBlock == null || clickedBlock.isEmpty();
        if (isNothing) {
            if (!ItemFeatureLinker.doFeatureAndReturn(event.getItem(), event, FeatureItemClick.class,
                    FeatureItemClick::handleItemClickNothing, DataItemClickNothing::new, true)) {
                event.setUseInteractedBlock(Result.DENY);
                event.setUseItemInHand(Result.DENY);
            }
        } else {
            switch (BlockFeatureLinker.doFeatureAndReturn(event.getClickedBlock(), event, FeatureBlockClick.class,
                    FeatureBlockClick::handleBlockClicked, DataBlockClicked::new, StateBlockClicked.Skipped)) {
                case Cancelled -> {
                    event.setUseInteractedBlock(Result.DENY);
                    event.setUseItemInHand(Result.DENY);
                    return;
                }
                case Use -> event.setUseInteractedBlock(Result.ALLOW);
                case Skipped -> {
                }
            }
            switch (ItemFeatureLinker.doFeatureAndReturn(event.getItem(), event, FeatureItemClick.class,
                    FeatureItemClick::handleItemClickBlock, DataItemClickBlock::new, StateItemClickBlock.Skipped)) {
                case Cancelled -> {
                    event.setUseInteractedBlock(Result.DENY);
                    event.setUseItemInHand(Result.DENY);
                    return;
                }
                case Use -> event.setUseItemInHand(Result.ALLOW);
                case Skipped -> {
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemInteractEntity(PlayerInteractEntityEvent event) {
        if (event == null || event.isCancelled())
            return;
        ItemStack itemStack = event.getPlayer().getInventory().getItem(Objects.requireNonNull(event.getHand()));
        if (!ItemFeatureLinker.doFeatureCancellable(itemStack, event, FeatureItemClick.class,
                FeatureItemClick::handleItemClickEntity, DataItemClickEntity::new)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemWear(PlayerArmorChangeEvent event) {
        if (event == null)
            return;
        ItemFeatureLinker.doFeature(event.getOldItem(), event, FeatureItemWear.class,
                FeatureItemWear::handleItemWearOff, DataItemWearOff::new);
        ItemFeatureLinker.doFeature(event.getNewItem(), event, FeatureItemWear.class, FeatureItemWear::handleItemWear,
                DataItemWear::new);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenPlayerJoin(PlayerJoinEvent event) {
        if (event == null)
            return;
        Player player = event.getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(BarleyTeaAPI.getInstance(), () -> {
            if (player.isOnline()) {
                ItemFeatureLinker.forEachEquipment(player, event,
                        Constants.ALL_SLOTS, FeatureItemHoldPlayerJoinOrQuit.class,
                        FeatureItemHoldPlayerJoinOrQuit::handleItemHoldPlayerJoin, DataItemHoldPlayerJoin::new);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenPlayerQuit(PlayerQuitEvent event) {
        if (event == null)
            return;
        ItemFeatureLinker.forEachEquipment(event.getPlayer(), event,
                Constants.ALL_SLOTS, FeatureItemHoldPlayerJoinOrQuit.class,
                FeatureItemHoldPlayerJoinOrQuit::handleItemHoldPlayerQuit, DataItemHoldPlayerQuit::new);
    }
}
