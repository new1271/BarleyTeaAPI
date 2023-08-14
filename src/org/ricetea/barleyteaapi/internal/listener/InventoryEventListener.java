package org.ricetea.barleyteaapi.internal.listener;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemAnvil;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemAnvilCombine;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemAnvilRename;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemAnvilRepair;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.api.item.render.AbstractItemRenderer;
import org.ricetea.barleyteaapi.internal.helper.ItemFeatureHelper;
import org.ricetea.barleyteaapi.util.ComponentUtil;
import org.ricetea.barleyteaapi.util.Lazy;

public final class InventoryEventListener implements Listener {
    private static final Lazy<InventoryEventListener> inst = new Lazy<>(InventoryEventListener::new);

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
        if (itemStack != null && BaseItem.isBarleyTeaItem(itemStack)) {
            Bukkit.getScheduler().runTaskLater(BarleyTeaAPI.getInstance(), () -> {
                AbstractItemRenderer.renderItem(event.getItem());
            }, 1);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemGrinding(PrepareGrindstoneEvent event) {
        if (event == null)
            return;
        ItemStack resultItem = event.getResult();
        if (resultItem == null || resultItem.getType().isAir())
            return;
        GrindstoneInventory inventory = event.getInventory();
        ItemStack newResultItem = ItemFeatureHelper.doItemRepair(inventory.getUpperItem(), inventory.getLowerItem(),
                resultItem);
        if (resultItem != newResultItem) {
            event.setResult(newResultItem);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemAnviled(PrepareAnvilEvent event) {
        if (event == null)
            return;
        ItemStack resultItem = event.getResult();
        if (resultItem == null || resultItem.getType().isAir())
            return;
        AnvilInventory inventory = event.getInventory();
        String renameText = inventory.getRenameText();
        ItemStack firstItem = inventory.getFirstItem();
        ItemStack secondItem = inventory.getSecondItem();
        if (firstItem != null) {
            NamespacedKey id = BaseItem.getItemID(firstItem);
            if (id != null) {
                BaseItem baseItem = ItemRegister.getInstance().lookupItemType(id);
                if (baseItem != null) {
                    final ItemStack oldResultItem = resultItem;
                    if (baseItem.isCertainItem(secondItem)) { //Repair mode
                        resultItem = ItemFeatureHelper.doItemRepair(inventory.getFirstItem(),
                                inventory.getSecondItem(), resultItem);
                        if (baseItem instanceof FeatureItemAnvil itemAnvilFeature) {
                            if (itemAnvilFeature.handleItemAnvilRepair(new DataItemAnvilRepair(event))) {
                                resultItem = event.getResult();
                            } else {
                                resultItem = null;
                            }
                        }
                    } else if (resultItem != null && BaseItem.isBarleyTeaItem(resultItem)) {
                        if (!ComponentUtil.translatableComponentEquals(firstItem.displayName(),
                                resultItem.displayName())) {
                            BaseItem.setName(resultItem, renameText);
                        }
                        //TODO: 放入自定義鐵砧事件
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
                        } else if (firstItem.getType().equals(secondItem.getType())) {
                            resultItem = null;
                        }
                    }
                    if (resultItem != null && BaseItem.isBarleyTeaItem(resultItem))
                        AbstractItemRenderer.renderItem(resultItem);
                    if (oldResultItem != resultItem) {
                        event.setResult(resultItem);
                    }
                }
            }
        } else {
            if (BaseItem.isBarleyTeaItem(secondItem)) {
                event.setResult(null);
            }
        }
    }
}
