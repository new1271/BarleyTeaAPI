package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;

public interface FeatureCommandGive {
    boolean handleCommandGive(@Nonnull ItemStack itemStackGived, @Nullable String nbt);
}
