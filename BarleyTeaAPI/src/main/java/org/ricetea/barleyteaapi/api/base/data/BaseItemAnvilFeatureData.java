package org.ricetea.barleyteaapi.api.base.data;

import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import java.util.Objects;

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
