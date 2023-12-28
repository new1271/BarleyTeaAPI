package org.ricetea.barleyteaapi.api.block.feature.data;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BaseFeatureData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public final class DataBlockClicked extends BaseFeatureData<PlayerInteractEvent> {

    public DataBlockClicked(@Nonnull PlayerInteractEvent event) {
        super(event);
    }

    @Nonnull
    public Block getBlock() {
        return Objects.requireNonNull(event.getClickedBlock());
    }

    @Nonnull
    public Player getPlayer() {
        return Objects.requireNonNull(event.getPlayer());
    }

    @Nullable
    public ItemStack getItemHoldByPlayer() {
        return event.getItem();
    }

    public boolean isHoldingItem() {
        return event.hasItem();
    }

    public boolean isHoldingBlock() {
        return event.isBlockInHand();
    }

    @Nullable
    public EquipmentSlot getHand() {
        return event.getHand();
    }

    @Nullable
    public Location getInteractionPoint() {
        return event.getInteractionPoint();
    }
}
