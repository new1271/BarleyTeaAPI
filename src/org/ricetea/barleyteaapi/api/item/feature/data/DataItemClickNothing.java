package org.ricetea.barleyteaapi.api.item.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BasePlayerFeatureData;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class DataItemClickNothing extends BasePlayerFeatureData<PlayerInteractEvent> {

    public DataItemClickNothing(@Nonnull PlayerInteractEvent event) {
        super(event);
    }

    public @Nonnull Action getAction() {
        return ObjectUtil.throwWhenNull(event.getAction());
    }

    public @Nonnull ItemStack getItemStack() {
        return ObjectUtil.throwWhenNull(event.getItem());
    }

    public @Nonnull EquipmentSlot getHand() {
        return ObjectUtil.throwWhenNull(event.getHand());
    }
}
