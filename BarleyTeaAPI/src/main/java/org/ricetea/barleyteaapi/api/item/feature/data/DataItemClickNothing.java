package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BasePlayerFeatureData;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataItemClickNothing extends BasePlayerFeatureData<PlayerInteractEvent> {

    public DataItemClickNothing(@Nonnull PlayerInteractEvent event) {
        super(event);
    }

    public boolean isLeftClick() {
        Action action = event.getAction();
        return action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK);
    }

    public boolean isRightClick() {
        Action action = event.getAction();
        return action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK);
    }

    public @Nonnull ItemStack getItemStack() {
        return Objects.requireNonNull(event.getItem());
    }

    public @Nonnull EquipmentSlot getHand() {
        return Objects.requireNonNull(event.getHand());
    }
}
