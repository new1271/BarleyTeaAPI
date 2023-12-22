package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BasePlayerFeatureData;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataItemClickEntity extends BasePlayerFeatureData<PlayerInteractEntityEvent> {

    public DataItemClickEntity(@Nonnull PlayerInteractEntityEvent event) {
        super(event);
    }

    public @Nonnull ItemStack getItemStack() {
        return Objects.requireNonNull(event.getPlayer().getInventory().getItem(event.getHand()));
    }

    public @Nonnull Entity getClickedEntity() {
        return Objects.requireNonNull(event.getRightClicked());
    }

    public @Nonnull EquipmentSlot getHand() {
        return Objects.requireNonNull(event.getHand());
    }
}
