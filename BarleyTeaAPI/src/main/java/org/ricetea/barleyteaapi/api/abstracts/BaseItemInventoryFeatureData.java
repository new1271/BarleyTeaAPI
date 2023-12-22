package org.ricetea.barleyteaapi.api.abstracts;

import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class BaseItemInventoryFeatureData<T extends InventoryEvent> extends BaseFeatureData<T> {

    public BaseItemInventoryFeatureData(@Nonnull T event) {
        super(event);
    }

    @Nonnull
    public Inventory getInventory() {
        return Objects.requireNonNull(event.getInventory());
    }
}
