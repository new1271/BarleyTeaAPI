package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public interface FeatureItemTick {
        void handleTickOnEquipment(@Nonnull Player player, @Nonnull PlayerInventory inventory,
                        @Nonnull ItemStack itemStack,
                        @Nonnull EquipmentSlot slot);

        void handleTickOnInventory(@Nonnull Player player, @Nonnull PlayerInventory inventory,
                        @Nonnull ItemStack itemStack,
                        int slot);
}
