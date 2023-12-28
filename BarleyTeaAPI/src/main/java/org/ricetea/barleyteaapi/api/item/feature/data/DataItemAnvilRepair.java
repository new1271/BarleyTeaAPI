package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BaseItemAnvilFeatureData;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataItemAnvilRepair extends BaseItemAnvilFeatureData {

    public DataItemAnvilRepair(@Nonnull PrepareAnvilEvent event) {
        super(event);
    }

    @Nonnull
    public ItemStack getItemStackCombined() {
        return Objects.requireNonNull(event.getInventory().getSecondItem());
    }
}
