package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BasePlayerFeatureData;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class DataItemLostFocus extends BasePlayerFeatureData<PlayerItemHeldEvent> {

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
                    .letNonNull(ObjectUtil.safeMap(itemStackGotFocus, BaseItem::getItemType),
                            DataItemType.empty());
            this.itemStackGotFocus = itemStackGotFocus;
        }
        return itemGotFocusType;
    }
}
