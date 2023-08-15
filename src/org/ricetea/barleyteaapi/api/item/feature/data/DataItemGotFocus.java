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

public final class DataItemGotFocus extends BaseFeatureData<PlayerItemHeldEvent> {

    @Nullable
    private DataItemType itemLostFocusType = null;

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

    @Nonnull
    public Player getPlayer() {
        return ObjectUtil.throwWhenNull(event.getPlayer());
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
    public DataItemType getItemStackLostFocusType() {
        DataItemType itemLostFocusType = this.itemLostFocusType;
        ItemStack itemStackLostFocus = getItemStackLostFocus();
        if (itemLostFocusType == null || this.itemStackLostFocus != itemStackLostFocus) {
            this.itemLostFocusType = itemLostFocusType = ObjectUtil
                    .letNonNull(ObjectUtil.mapWhenNonnull(itemStackLostFocus, BaseItem::getItemType),
                            DataItemType.empty());
            this.itemStackLostFocus = itemStackLostFocus;
        }
        return itemLostFocusType;
    }
}