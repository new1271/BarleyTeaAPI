package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.ricetea.barleyteaapi.api.base.data.BaseItemHoldEntityFeatureData;
import org.ricetea.barleyteaapi.api.block.CustomBlockType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataItemHoldPlayerBreakBlock extends BaseItemHoldEntityFeatureData<BlockBreakEvent> {
    @Nonnull
    private final Lazy<CustomBlockType> blockType;

    public DataItemHoldPlayerBreakBlock(@Nonnull BlockBreakEvent event) {
        super(event, Objects.requireNonNull(event.getPlayer()),
                Objects.requireNonNull(event.getPlayer().getEquipment().getItemInMainHand()),
                EquipmentSlot.HAND);
        blockType = Lazy.create(() -> CustomBlockType.get(getBlock()));
    }

    public @Nonnull Block getBlock() {
        return Objects.requireNonNull(event.getBlock());
    }

    public @Nonnull CustomBlockType getBlockType() {
        return blockType.get();
    }

    public @Nonnull Player getEntity() {
        return Objects.requireNonNull(event.getPlayer());
    }

    public @Nonnull Player getPlayer() {
        return Objects.requireNonNull(event.getPlayer());
    }

    public void setDropItems(boolean dropItems) {
        event.setDropItems(dropItems);
    }

    public boolean isDropItems() {
        return event.isDropItems();
    }
}
