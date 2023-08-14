package org.ricetea.barleyteaapi.api.item.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemInventoryResultFeatureData;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class DataItemGrindstone extends BaseItemInventoryResultFeatureData<PrepareGrindstoneEvent> {

    public DataItemGrindstone(@Nonnull PrepareGrindstoneEvent event) {
        super(event);
    }

    @Nonnull
    public GrindstoneInventory getInventory() {
        return ObjectUtil.throwWhenNull(event.getInventory());
    }
}
