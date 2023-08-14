package org.ricetea.barleyteaapi.api.item.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemAnvilFeatureData;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class DataItemAnvilRepair extends BaseItemAnvilFeatureData {

    public DataItemAnvilRepair(@Nonnull PrepareAnvilEvent event) {
        super(event);
    }

    @Nonnull
    public ItemStack getItemStackCombined() {
        return ObjectUtil.throwWhenNull(event.getInventory().getSecondItem());
    }
}
