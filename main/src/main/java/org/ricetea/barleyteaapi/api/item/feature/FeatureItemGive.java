package org.ricetea.barleyteaapi.api.item.feature;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public interface FeatureItemGive {
    @Nullable
    ItemStack handleItemGive(int count);
}
