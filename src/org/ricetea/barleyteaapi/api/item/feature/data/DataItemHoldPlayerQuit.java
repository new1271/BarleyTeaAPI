package org.ricetea.barleyteaapi.api.item.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemHoldEntityFeatureData;

public final class DataItemHoldPlayerQuit extends BaseItemHoldEntityFeatureData<PlayerQuitEvent> {

    @SuppressWarnings("null")
    public DataItemHoldPlayerQuit(@Nonnull PlayerQuitEvent event, @Nonnull ItemStack itemStack,
            @Nonnull EquipmentSlot equipmentSlot) {
        super(event, event.getPlayer(), itemStack, equipmentSlot);
    }
}
