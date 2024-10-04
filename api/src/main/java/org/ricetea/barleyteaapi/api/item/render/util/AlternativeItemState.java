package org.ricetea.barleyteaapi.api.item.render.util;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface AlternativeItemState {
    @Nonnull
    static AlternativeItemState getInstance() {
        return Objects.requireNonNull(getInstanceUnsafe());
    }

    @Nullable
    static AlternativeItemState getInstanceUnsafe() {
        return Bukkit.getServicesManager().load(AlternativeItemState.class);
    }

    @Nonnull
    default ItemStack store(@Nonnull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            itemStack.setItemMeta(store(meta));
        }
        return itemStack;
    }

    @Nonnull
    ItemMeta store(@Nonnull ItemMeta meta);

    @Nonnull
    default ItemStack restore(@Nonnull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            itemStack.setItemMeta(restore(meta));
        }
        return itemStack;
    }

    @Nonnull
    ItemMeta restore(@Nonnull ItemMeta meta);

}
