package org.ricetea.barleyteaapi.api.item.recipe;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@FunctionalInterface
public interface SmithingFunction {
    @Nullable
    ItemStack apply(@Nonnull ItemStack original, @Nonnull ItemStack template, @Nonnull ItemStack addition);
}
