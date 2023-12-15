package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;

public interface FeatureItemGive {
    @Nullable
    ItemStack handleItemGive(int count);
}
