package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;

import org.bukkit.inventory.ItemStack;

public interface FeatureCustomDurability {
    int getMaxDurability(@Nonnull ItemStack itemStack);

    int getDurabilityDamage(@Nonnull ItemStack itemStack);

    void setDurabilityDamage(@Nonnull ItemStack itemStack, int damage);
}
