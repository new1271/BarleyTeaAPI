package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class DataCommandGive {

    @Nonnull
    private final ItemStack itemStack;

    @Nonnull
    private final String nbt;

    public DataCommandGive(@Nonnull ItemStack itemStack, @Nullable String nbt) {
        this.itemStack = itemStack;
        this.nbt = nbt == null ? "" : nbt;
    }

    @Nonnull
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Nonnull
    public String getNBT() {
        return nbt;
    }
}
