package org.ricetea.barleyteaapi.api.item.feature.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;

public final class DataCommandGive{

    @Nonnull
    private final ItemStack itemStack;

    @Nonnull
    private final String nbt;

    public DataCommandGive(@Nonnull ItemStack itemStack, @Nullable String nbt) {
        this.itemStack = itemStack;
        this.nbt = nbt == null ? "" : nbt;
    }

    @Nonnull
    public final ItemStack getItemStack() {
        return itemStack;
    }

    @Nonnull
    public final String getNBT() {
        return nbt;
    }
}

