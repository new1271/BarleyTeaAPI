package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BasePlayerFeatureData;
import org.ricetea.barleyteaapi.api.block.data.DataBlockType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataItemClickBlock extends BasePlayerFeatureData<PlayerInteractEvent> {

    @Nonnull
    private final Lazy<DataBlockType> blockType;

    public DataItemClickBlock(@Nonnull PlayerInteractEvent event) {
        super(event);
        blockType = Lazy.create(() -> BaseBlock.getBlockType(getClickedBlock()));
    }

    public boolean isLeftClick() {
        Action action = event.getAction();
        return action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK);
    }

    public boolean isRightClick() {
        Action action = event.getAction();
        return action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK);
    }

    public boolean isInteractableBlock() {
        Block block = event.getClickedBlock();
        if (block != null) {
            Material type = block.getType();
            return type.isInteractable() && !Tag.STAIRS.isTagged(type) && !Tag.FENCES.isTagged(type);
        }
        return false;
    }

    public @Nonnull ItemStack getItemStack() {
        return Objects.requireNonNull(event.getItem());
    }

    public @Nonnull Block getClickedBlock() {
        return Objects.requireNonNull(event.getClickedBlock());
    }

    public @Nonnull DataBlockType getClickedBlockType() {
        return blockType.get();
    }

    public @Nonnull EquipmentSlot getHand() {
        return Objects.requireNonNull(event.getHand());
    }

    public @Nonnull BlockFace getBlockFace() {
        return Objects.requireNonNull(event.getBlockFace());
    }
}
