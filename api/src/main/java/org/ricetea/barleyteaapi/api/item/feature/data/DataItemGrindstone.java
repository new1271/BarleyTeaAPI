package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.ricetea.barleyteaapi.api.base.data.BaseItemInventoryResultFeatureData;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataItemGrindstone extends BaseItemInventoryResultFeatureData<PrepareGrindstoneEvent> {

    public DataItemGrindstone(@Nonnull PrepareGrindstoneEvent event) {
        super(event);
    }

    @Nonnull
    public GrindstoneInventory getInventory() {
        return Objects.requireNonNull(event.getInventory());
    }
}
