package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseFeatureData;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataItemBroken extends BaseFeatureData<PlayerItemBreakEvent> {

    public DataItemBroken(@Nonnull PlayerItemBreakEvent event) {
        super(event);
    }

    public @Nonnull Player getPlayer() {
        return Objects.requireNonNull(event.getPlayer());
    }

    public @Nonnull ItemStack getItemStack() {
        return Objects.requireNonNull(event.getBrokenItem());
    }
}
