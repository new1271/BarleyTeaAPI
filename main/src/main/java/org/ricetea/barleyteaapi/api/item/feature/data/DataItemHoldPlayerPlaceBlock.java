package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseItemHoldEntityFeatureData;
import org.ricetea.barleyteaapi.api.block.CustomBlockType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataItemHoldPlayerPlaceBlock extends BaseItemHoldEntityFeatureData<BlockPlaceEvent> {
    @Nonnull
    private final Lazy<CustomBlockType> blockType;

    public DataItemHoldPlayerPlaceBlock(@Nonnull BlockPlaceEvent event) {
        super(event, Objects.requireNonNull(event.getPlayer()), Objects.requireNonNull(event.getItemInHand()),
                Objects.requireNonNull(event.getHand()));
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

    public @Nonnull BlockState getReplacedBlockState() {
        return Objects.requireNonNull(event.getBlockReplacedState());
    }

    public @Nonnull Block getBlockAgainst() {
        return Objects.requireNonNull(event.getBlockAgainst());
    }

    public boolean canBuild() {
        return event.canBuild();
    }

    public void setBuild(boolean canBuild) {
        event.setBuild(canBuild);
    }
}
