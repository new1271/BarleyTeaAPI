package org.ricetea.barleyteaapi.api.item.feature.data;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseFeatureData;

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
