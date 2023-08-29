package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;

import org.bukkit.inventory.ItemStack;

public interface FeatureBarleyTeaAPILoad {
    void handleAPILoaded(@Nonnull ItemStack itemStack);

    void handleAPIUnloaded(@Nonnull ItemStack itemStack);
}
