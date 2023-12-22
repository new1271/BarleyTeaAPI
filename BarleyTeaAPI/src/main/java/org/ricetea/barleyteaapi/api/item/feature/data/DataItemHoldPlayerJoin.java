package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemHoldEntityFeatureData;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataItemHoldPlayerJoin extends BaseItemHoldEntityFeatureData<PlayerJoinEvent> {

    public DataItemHoldPlayerJoin(@Nonnull PlayerJoinEvent event, @Nonnull ItemStack itemStack,
                                  @Nonnull EquipmentSlot equipmentSlot) {
        super(event, event.getPlayer(), itemStack, equipmentSlot);
    }

    @Nonnull
    public Player getEntity() {
        return Objects.requireNonNull(event.getPlayer());
    }

    @Nonnull
    public Player getPlayer() {
        return Objects.requireNonNull(event.getPlayer());
    }
}
