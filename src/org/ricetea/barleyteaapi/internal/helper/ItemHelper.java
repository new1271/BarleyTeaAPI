package org.ricetea.barleyteaapi.internal.helper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class ItemHelper {
    @Nonnull
    public static ItemStack getSingletonClone(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return new ItemStack(Material.AIR);
        } else if (itemStack.getType().isAir()) {
            return itemStack;
        } else {
            ItemStack result = itemStack.clone();
            if (result.getAmount() > 1)
                result.setAmount(1);
            return result;
        }
    }
}
