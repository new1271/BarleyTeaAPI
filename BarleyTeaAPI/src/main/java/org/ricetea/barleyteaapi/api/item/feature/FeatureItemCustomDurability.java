package org.ricetea.barleyteaapi.api.item.feature;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface FeatureItemCustomDurability {
    int getMaxDurability(@Nonnull ItemStack itemStack);

    int getDurabilityDamage(@Nonnull ItemStack itemStack);

    void setDurabilityDamage(@Nonnull ItemStack itemStack, int damage);
}
