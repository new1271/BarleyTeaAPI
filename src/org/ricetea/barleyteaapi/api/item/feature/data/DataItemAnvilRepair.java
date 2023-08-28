package org.ricetea.barleyteaapi.api.item.feature.data;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemAnvilFeatureData;

public final class DataItemAnvilRepair extends BaseItemAnvilFeatureData {

    public DataItemAnvilRepair(@Nonnull PrepareAnvilEvent event) {
        super(event);
    }

    @Nonnull
    public ItemStack getItemStackCombined() {
        return Objects.requireNonNull(event.getInventory().getSecondItem());
    }
}
