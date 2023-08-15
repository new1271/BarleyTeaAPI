package org.ricetea.barleyteaapi.api.item.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BasePlayerFeatureData;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class DataItemClickBlock extends BasePlayerFeatureData<PlayerInteractEvent> {

    public DataItemClickBlock(@Nonnull PlayerInteractEvent event) {
        super(event);
    }

    public boolean isLeftClick() {
        Action action = event.getAction();
        return action != null && (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK));
    }

    public boolean isRightClick() {
        Action action = event.getAction();
        return action != null && (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK));
    }

    public @Nonnull ItemStack getItemStack() {
        return ObjectUtil.throwWhenNull(event.getItem());
    }

    public @Nonnull Block getClickedBlock() {
        return ObjectUtil.throwWhenNull(event.getClickedBlock());
    }

    public @Nonnull Material getClickedBlockType() {
        return ObjectUtil.letNonNull(getClickedBlock().getType(), Material.AIR);
    }

    public @Nonnull EquipmentSlot getHand() {
        return ObjectUtil.throwWhenNull(event.getHand());
    }

    public @Nonnull BlockFace getBlockFace() {
        return ObjectUtil.throwWhenNull(event.getBlockFace());
    }
}
