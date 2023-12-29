package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BasePlayerFeatureData;
import org.ricetea.barleyteaapi.api.item.CustomItemType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public final class DataItemConsume extends BasePlayerFeatureData<PlayerItemConsumeEvent> {
    @Nullable
    private CustomItemType replacementType = null;

    public DataItemConsume(@Nonnull PlayerItemConsumeEvent event) {
        super(event);
    }

    public @Nonnull ItemStack getItem() {
        return Objects.requireNonNull(event.getItem());
    }

    public void setItem(@Nullable ItemStack item) {
        event.setItem(item);
    }

    public @Nonnull EquipmentSlot getHand() {
        return Objects.requireNonNull(event.getHand());
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

    public void setReplacement(@Nullable ItemStack replacement) {
        event.setReplacement(replacement);
        replacementType = null;
    }

    public @Nonnull CustomItemType getReplacementType() {
        CustomItemType replacementType = this.replacementType;
        if (replacementType == null)
            this.replacementType = replacementType = CustomItemType.get(getReplacement());
        return replacementType;
    }
}
