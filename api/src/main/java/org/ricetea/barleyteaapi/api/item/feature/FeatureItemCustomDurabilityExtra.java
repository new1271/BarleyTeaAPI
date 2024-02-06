package org.ricetea.barleyteaapi.api.item.feature;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface FeatureItemCustomDurabilityExtra extends FeatureItemCustomDurability {
    boolean isAlwaysShowDurabilityHint(@Nonnull ItemStack itemStack);
}
