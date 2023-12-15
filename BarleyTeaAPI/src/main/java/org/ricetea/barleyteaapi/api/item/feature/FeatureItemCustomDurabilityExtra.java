package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;

import org.bukkit.inventory.ItemStack;

public interface FeatureItemCustomDurabilityExtra extends FeatureItemCustomDurability {
    boolean isAlwaysShowDurabilityHint(@Nonnull ItemStack itemStack);
}
