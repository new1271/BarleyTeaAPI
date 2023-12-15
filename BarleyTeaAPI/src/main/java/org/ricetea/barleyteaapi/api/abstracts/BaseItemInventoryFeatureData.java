package org.ricetea.barleyteaapi.api.abstracts;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;

public abstract class BaseItemInventoryFeatureData<T extends InventoryEvent> extends BaseFeatureData<T> {

    public BaseItemInventoryFeatureData(@Nonnull T event) {
        super(event);
    }

    @Nonnull
    public Inventory getInventory() {
        return Objects.requireNonNull(event.getInventory());
    }
}
