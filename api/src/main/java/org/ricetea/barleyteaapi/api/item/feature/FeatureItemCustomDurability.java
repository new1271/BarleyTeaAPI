package org.ricetea.barleyteaapi.api.item.feature;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface FeatureItemCustomDurability extends ItemFeature {
    int getMaxDurability(@Nonnull ItemStack itemStack);

    int getDurabilityDamage(@Nonnull ItemStack itemStack);

    void setDurabilityDamage(@Nonnull ItemStack itemStack, int damage);
}
