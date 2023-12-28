package org.ricetea.barleyteaapi.api.block.feature.data;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BaseBlockFeatureData;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataBlockPlaceByPlayer extends BaseBlockFeatureData<BlockPlaceEvent> {

    private final Lazy<DataItemType> itemType;

    public DataBlockPlaceByPlayer(@Nonnull BlockPlaceEvent event) {
        super(event);
        itemType = Lazy.create(() -> BaseItem.getItemType(getItemInHand()));
    }

    public @Nonnull Player getPlayer() {
        return Objects.requireNonNull(event.getPlayer());
    }

    public @Nonnull BlockState getReplacedBlockState() {
        return Objects.requireNonNull(event.getBlockReplacedState());
    }

    public @Nonnull Block getBlockAgainst() {
        return Objects.requireNonNull(event.getBlockAgainst());
    }

    public @Nonnull ItemStack getItemInHand() {
        return Objects.requireNonNull(event.getItemInHand());
    }

    public @Nonnull DataItemType getItemInHandType() {
        return itemType.get();
    }

    public @Nonnull EquipmentSlot getHand() {
        return Objects.requireNonNull(event.getHand());
    }

    public boolean canBuild() {
        return event.canBuild();
    }

    public void setBuild(boolean canBuild) {
        event.setBuild(canBuild);
    }
}
