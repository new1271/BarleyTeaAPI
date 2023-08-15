package org.ricetea.barleyteaapi.api.item.feature.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BasePlayerFeatureData;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class DataItemConsume extends BasePlayerFeatureData<PlayerItemConsumeEvent> {
    @Nullable
    private DataItemType replacementType = null;

    public DataItemConsume(@Nonnull PlayerItemConsumeEvent event) {
        super(event);
    }

    public @Nonnull ItemStack getItem() {
        return ObjectUtil.throwWhenNull(event.getItem());
    }

    public void setItem(@Nullable ItemStack item) {
        event.setItem(item);
    }

    public @Nonnull EquipmentSlot getHand() {
        return ObjectUtil.throwWhenNull(event.getHand());
    }

    public boolean isOffHand() {
        return event.getHand().equals(EquipmentSlot.OFF_HAND);
    }

    public boolean isMainHand() {
        return event.getHand().equals(EquipmentSlot.HAND);
    }

    public @Nullable ItemStack getReplacement() {
        return event.getReplacement();
    }

    public @Nonnull DataItemType getReplacementType() {
        DataItemType replacementType = this.replacementType;
        if (replacementType == null)
            this.replacementType = replacementType = ObjectUtil
                    .letNonNull(ObjectUtil.mapWhenNonnull(getReplacement(), BaseItem::getItemType),
                            DataItemType.empty());
        return replacementType;
    }

    public void setReplacement(@Nullable ItemStack replacement) {
        event.setReplacement(replacement);
        replacementType = null;
    }
}
