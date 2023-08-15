package org.ricetea.barleyteaapi.api.item.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BasePlayerFeatureData;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class DataItemClickEntity extends BasePlayerFeatureData<PlayerInteractEntityEvent> {

    public DataItemClickEntity(@Nonnull PlayerInteractEntityEvent event) {
        super(event);
    }

    public @Nonnull ItemStack getItemStack() {
        return ObjectUtil.throwWhenNull(event.getPlayer().getInventory().getItem(event.getHand()));
    }

    public @Nonnull Entity getClickedEntity() {
        return ObjectUtil.throwWhenNull(event.getRightClicked());
    }

    public @Nonnull EquipmentSlot getHand() {
        return ObjectUtil.throwWhenNull(event.getHand());
    }
}
