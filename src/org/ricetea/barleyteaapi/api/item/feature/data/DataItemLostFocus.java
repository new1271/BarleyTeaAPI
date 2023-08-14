package org.ricetea.barleyteaapi.api.item.feature.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseFeatureData;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class DataItemLostFocus extends BaseFeatureData<PlayerItemHeldEvent> {

    @Nullable
    private DataItemType itemGotFocusType = null;

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

    @Nonnull
    public Player getPlayer() {
        return ObjectUtil.throwWhenNull(event.getPlayer());
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
    public DataItemType getItemStackGotFocusType() {
        DataItemType itemGotFocusType = this.itemGotFocusType;
        ItemStack itemStackGotFocus = getItemStackGotFocus();
        if (itemGotFocusType == null || this.itemStackGotFocus != itemStackGotFocus) {
            this.itemGotFocusType = itemGotFocusType = ObjectUtil
                    .letNonNull(ObjectUtil.mapWhenNonnull(itemStackGotFocus, BaseItem::getItemType),
                            DataItemType.empty());
            this.itemStackGotFocus = itemStackGotFocus;
        }
        return itemGotFocusType;
    }
}
