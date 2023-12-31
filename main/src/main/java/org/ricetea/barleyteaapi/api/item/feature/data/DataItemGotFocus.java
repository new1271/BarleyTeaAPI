package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BasePlayerFeatureData;
import org.ricetea.barleyteaapi.api.item.CustomItemType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class DataItemGotFocus extends BasePlayerFeatureData<PlayerItemHeldEvent> {

    @Nullable
    private CustomItemType itemLostFocusType = null;

    @Nullable
    private ItemStack itemStackLostFocus;

    public DataItemGotFocus(@Nonnull PlayerItemHeldEvent event) {
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
        return getPlayer().getInventory().getItem(event.getNewSlot());
    }

    public void setItemStack(@Nullable ItemStack itemStack) {
        getPlayer().getInventory().setItem(event.getNewSlot(), itemStack);
    }

    @Nullable
    public ItemStack getItemStackLostFocus() {
        return getPlayer().getInventory().getItem(event.getPreviousSlot());
    }

    public void setItemStackLostFocus(@Nullable ItemStack itemStack) {
        itemLostFocusType = null;
        getPlayer().getInventory().setItem(event.getPreviousSlot(), itemStack);
    }

    @Nonnull
    public CustomItemType getItemStackLostFocusType() {
        CustomItemType itemLostFocusType = this.itemLostFocusType;
        ItemStack itemStackLostFocus = getItemStackLostFocus();
        if (itemLostFocusType == null || this.itemStackLostFocus != itemStackLostFocus) {
            this.itemLostFocusType = itemLostFocusType = CustomItemType.get(itemStackLostFocus);
            this.itemStackLostFocus = itemStackLostFocus;
        }
        return itemLostFocusType;
    }
}
