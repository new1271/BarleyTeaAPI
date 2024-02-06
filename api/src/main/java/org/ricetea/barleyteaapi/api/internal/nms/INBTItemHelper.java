package org.ricetea.barleyteaapi.api.internal.nms;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;

@ApiStatus.Internal
public interface INBTItemHelper extends IHelper {
    @Nonnull
    ItemStack copyNbtWhenSmithing(@Nonnull ItemStack original, @Nonnull ItemStack itemStackCopying);
}
