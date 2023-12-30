package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BasePlayerFeatureData;
import org.ricetea.barleyteaapi.api.item.CustomItemType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class DataItemLostFocus extends BasePlayerFeatureData<PlayerItemHeldEvent> {

    @Nullable
    private CustomItemType itemGotFocusType = null;

    @Nullable
    private ItemStack itemStackGotFocus;

    public DataItemLostFocus(@Nonnull PlayerItemHeldEvent event) {
        super(event);
    }

    public int getPreviousSlot() {
        return event.getPreviousSlot();
    }

    public int getNewSlot() {
        return event.getNewSlot();
    }

    @Nullable
    public ItemStack getItemStack() {
        return getPlayer().getInventory().getItem(event.getPreviousSlot());
    }

    public void setItemStack(@Nullable ItemStack itemStack) {
        getPlayer().getInventory().setItem(event.getPreviousSlot(), itemStack);
    }

    @Nullable
    public ItemStack getItemStackGotFocus() {
        return getPlayer().getInventory().getItem(event.getNewSlot());
    }

    public void setItemStackGotFocus(@Nullable ItemStack itemStack) {
        itemGotFocusType = null;
        getPlayer().getInventory().setItem(event.getNewSlot(), itemStack);
    }

    @Nonnull
    public CustomItemType getItemStackGotFocusType() {
        CustomItemType itemGotFocusType = this.itemGotFocusType;
        ItemStack itemStackGotFocus = getItemStackGotFocus();
        if (itemGotFocusType == null || this.itemStackGotFocus != itemStackGotFocus) {
            this.itemGotFocusType = itemGotFocusType = CustomItemType.get(itemStackGotFocus);
            this.itemStackGotFocus = itemStackGotFocus;
        }
        return itemGotFocusType;
    }
}
