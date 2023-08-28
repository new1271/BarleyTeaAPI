package org.ricetea.barleyteaapi.api.abstracts;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public abstract class BaseItemAnvilFeatureData extends BaseItemInventoryResultFeatureData<PrepareAnvilEvent> {

    public BaseItemAnvilFeatureData(@Nonnull PrepareAnvilEvent event) {
        super(event);
    }

    @Nonnull
    public ItemStack getItemStack() {
        return Objects.requireNonNull(event.getInventory().getFirstItem());
    }

    @Nonnull
    public String getRenameText() {
        return ObjectUtil.letNonNull(event.getInventory().getRenameText(), "");
    }

    @Nonnull
    public AnvilInventory getInventory() {
        return Objects.requireNonNull(event.getInventory());
    }
}
