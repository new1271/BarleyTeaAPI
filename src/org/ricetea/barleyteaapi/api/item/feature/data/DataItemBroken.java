package org.ricetea.barleyteaapi.api.item.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseFeatureData;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class DataItemBroken extends BaseFeatureData<PlayerItemBreakEvent> {

    public DataItemBroken(@Nonnull PlayerItemBreakEvent event) {
        super(event);
    }

    public @Nonnull Player getPlayer() {
        return ObjectUtil.throwWhenNull(event.getPlayer());
    }

    public @Nonnull ItemStack getItemStack() {
        return ObjectUtil.throwWhenNull(event.getBrokenItem());
    }
}
