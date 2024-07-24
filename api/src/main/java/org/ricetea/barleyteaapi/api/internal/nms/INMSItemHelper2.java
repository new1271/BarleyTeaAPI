package org.ricetea.barleyteaapi.api.internal.nms;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface INMSItemHelper2 {
    @Nonnull
    static INMSItemHelper2 getInstance() {
        return Objects.requireNonNull(Bukkit.getServicesManager().load(INMSItemHelper2.class));
    }

    @Nullable
    static INMSItemHelper2 getInstanceUnsafe() {
        return Bukkit.getServicesManager().load(INMSItemHelper2.class);
    }

    default void applyCustomDurabilityBar(@Nonnull ItemStack itemStack, int damage, int maxDurability){
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return;
        applyCustomDurabilityBar(meta, damage, maxDurability);
        itemStack.setItemMeta(meta);
    }

    void applyCustomDurabilityBar(@Nonnull ItemMeta itemMeta, int damage, int maxDurability);

    default void restoreCustomDurabilityBar(@Nonnull ItemStack itemStack){
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return;
        restoreCustomDurabilityBar(meta, itemStack.getType().getMaxDurability());
        itemStack.setItemMeta(meta);
    }

    void restoreCustomDurabilityBar(@Nonnull ItemMeta itemMeta, int maxDurability);
}
