package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public final class DataItemSlotFilter {

    @Nonnull
    private final ItemStack itemStack;

    @Nonnull
    private final EquipmentSlot slot;

    public DataItemSlotFilter(@Nonnull ItemStack itemStack, @Nonnull EquipmentSlot slot) {
        this.itemStack = itemStack;
        this.slot = slot;
    }

    @Nonnull
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Nonnull
    public EquipmentSlot getEquipmentSlot() {
        return slot;
    }

    public boolean isHand() {
        return slot.isHand();
    }

    public boolean isArmor() {
        return slot.isArmor();
    }
}
