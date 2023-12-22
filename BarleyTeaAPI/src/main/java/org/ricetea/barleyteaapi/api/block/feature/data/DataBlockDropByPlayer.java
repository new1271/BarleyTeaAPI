package org.ricetea.barleyteaapi.api.block.feature.data;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDropItemEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseBlockFeatureData;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public final class DataBlockDropByPlayer extends BaseBlockFeatureData<BlockDropItemEvent> {

    public DataBlockDropByPlayer(@Nonnull BlockDropItemEvent event) {
        super(event);
    }

    public @Nonnull Player getPlayer() {
        return Objects.requireNonNull(event.getPlayer());
    }

    public @Nonnull BlockState getBlockState() {
        return Objects.requireNonNull(event.getBlockState());
    }

    public @Nonnull List<Item> getItems() {
        return Objects.requireNonNull(event.getItems());
    }

}
