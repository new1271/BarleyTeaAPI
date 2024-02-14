package org.ricetea.barleyteaapi.api.item.feature;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;

public interface FeatureItemTick extends ItemFeature {
    void handleTickOnEquipment(@Nonnull Player player, @Nonnull PlayerInventory inventory,
                               @Nonnull ItemStack itemStack,
                               @Nonnull EquipmentSlot slot);

    void handleTickOnInventory(@Nonnull Player player, @Nonnull PlayerInventory inventory,
                               @Nonnull ItemStack itemStack,
                               int slot);
}
