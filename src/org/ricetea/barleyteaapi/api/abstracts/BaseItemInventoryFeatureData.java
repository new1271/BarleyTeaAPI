package org.ricetea.barleyteaapi.api.abstracts;

import javax.annotation.Nonnull;

import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public abstract class BaseItemInventoryFeatureData<T extends InventoryEvent> extends BaseFeatureData<T> {

    public BaseItemInventoryFeatureData(@Nonnull T event) {
        super(event);
    }

    @Nonnull
    public Inventory getInventory() {
        return ObjectUtil.throwWhenNull(event.getInventory());
    }
}
