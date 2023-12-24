package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.*;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemAnvil;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemEnchant;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGrindstone;
import org.ricetea.barleyteaapi.api.item.feature.data.*;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.internal.linker.ItemFeatureLinker;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public final class InventoryEventListener implements Listener {
    private static final Lazy<InventoryEventListener> inst = Lazy.create(InventoryEventListener::new);

    private InventoryEventListener() {
    }

    @Nonnull
    public static InventoryEventListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemEnchanting(EnchantItemEvent event) {
        if (event == null || event.isCancelled())
            return;
        ItemStack itemStack = event.getItem();
        if (itemStack != null) {
            NamespacedKey id = BaseItem.getItemID(itemStack);
            ItemRegister register = ItemRegister.getInstanceUnsafe();
            if (register != null && id != null) {
                BaseItem baseItem = register.lookup(id);
                if (baseItem != null) {
                    Consumer<ItemStack> job = null;
                    if (baseItem instanceof FeatureItemEnchant itemEnchantFeature) {
                        DataItemEnchant data = new DataItemEnchant(event);
                        itemEnchantFeature.handleItemEnchant(data);
                        job = data.getJobAfterItemEnchant();
                    }
                    final Consumer<ItemStack> finalJob = job;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(BarleyTeaAPI.getInstance(),
                            () -> {
                                ItemStack _itemStack = event.getItem();
                                if (finalJob != null) {
                                    finalJob.accept(_itemStack);
                                }
                            });
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemGrinding(PrepareGrindstoneEvent event) {
        if (event == null)
            return;
        ItemStack resultItem = event.getResult();
        if (resultItem == null || resultItem.getType().isAir())
            return;
        final GrindstoneInventory inventory = event.getInventory();
        final ItemStack upperItem = inventory.getUpperItem();
        final ItemStack lowerItem = inventory.getLowerItem();
        if (upperItem != null) {
            NamespacedKey id = BaseItem.getItemID(upperItem);
            ItemRegister register = ItemRegister.getInstanceUnsafe();
            if (register != null && id != null) {
                BaseItem baseItem = register.lookup(id);
                if (baseItem != null) {
                    final ItemStack oldResultItem = resultItem;
                    if (baseItem.isCertainItem(lowerItem)) {
                        resultItem = ItemFeatureLinker.doItemRepair(upperItem, lowerItem, resultItem);
                        if (baseItem instanceof FeatureItemGrindstone itemGrindstoneFeature) {
                            if (itemGrindstoneFeature.handleItemGrindstone(new DataItemGrindstone(event))) {
                                resultItem = event.getResult();
                            } else {
                                resultItem = null;
                            }
                        }
                    }
                    if (oldResultItem != resultItem) {
                        event.setResult(resultItem);
                    }
                    return;
                }
            }
        }
        if (BaseItem.isBarleyTeaItem(lowerItem)) {
            event.setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemAnviled(PrepareAnvilEvent event) {
        if (event == null)
            return;
        ItemStack resultItem = event.getResult();
        if (resultItem == null || resultItem.getType().isAir())
            return;
        final AnvilInventory inventory = event.getInventory();
        final ItemStack firstItem = inventory.getFirstItem();
        final ItemStack secondItem = inventory.getSecondItem();
        if (firstItem != null) {
            NamespacedKey id = BaseItem.getItemID(firstItem);
            ItemRegister register = ItemRegister.getInstanceUnsafe();
            if (register != null && id != null) {
                BaseItem baseItem = register.lookup(id);
                if (baseItem != null) {
                    final ItemStack oldResultItem = resultItem;
                    if (baseItem.isCertainItem(secondItem)) { //Repair mode
                        resultItem = ItemFeatureLinker.doItemRepair(firstItem, secondItem, resultItem);
                        if (baseItem instanceof FeatureItemAnvil itemAnvilFeature) {
                            if (itemAnvilFeature.handleItemAnvilRepair(new DataItemAnvilRepair(event))) {
                                resultItem = event.getResult();
                            } else {
                                resultItem = null;
                            }
                        }
                    } else if (resultItem != null && BaseItem.isBarleyTeaItem(resultItem)) {
                        if (baseItem instanceof FeatureItemAnvil itemAnvilFeature) {
                            if (secondItem != null && !secondItem.getType().isAir()) { //Combine mode
                                if (itemAnvilFeature.handleItemAnvilCombine(new DataItemAnvilCombine(event))) {
                                    resultItem = event.getResult();
                                } else {
                                    resultItem = null;
                                }
                            } else { //Rename Mode
                                if (itemAnvilFeature.handleItemAnvilRename(new DataItemAnvilRename(event))) {
                                    resultItem = event.getResult();
                                } else {
                                    resultItem = null;
                                }
                            }
                        } else if (secondItem != null && firstItem.getType().equals(secondItem.getType())) {
                            resultItem = null;
                        }
                    }
                    if (oldResultItem != resultItem) {
                        event.setResult(resultItem);
                    }
                    return;
                }
            }
        }
        if (BaseItem.isBarleyTeaItem(secondItem)) {
            event.setResult(null);
        }
    }
}
