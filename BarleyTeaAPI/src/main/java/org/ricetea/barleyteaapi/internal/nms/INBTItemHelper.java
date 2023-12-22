package org.ricetea.barleyteaapi.internal.nms;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface INBTItemHelper extends IHelper {
    @Nonnull
    ItemStack copyNbt(@Nonnull ItemStack original, @Nonnull ItemStack itemStackCopying);
}
