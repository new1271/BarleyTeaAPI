package org.ricetea.barleyteaapi.api.block.feature.data;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseBlockFeatureData;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataBlockBreakByPlayer extends BaseBlockFeatureData<BlockBreakEvent> {

    public DataBlockBreakByPlayer(@Nonnull BlockBreakEvent event) {
        super(event);
    }

    public @Nonnull Player getPlayer() {
        return Objects.requireNonNull(event.getPlayer());
    }

    public boolean isDropItems() {
        return event.isDropItems();
    }

    public void setDropItems(boolean dropItems) {
        event.setDropItems(dropItems);
    }

    public int getExpToDrop() {
        return event.getExpToDrop();
    }

    public void setExpToDrop(int exp) {
        event.setExpToDrop(exp);
    }
}
