package org.ricetea.barleyteaapi.api.internal.nms;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
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

    @Nonnull
    default ItemStack applyCustomDurabilityBar(@Nonnull ItemStack itemStack, int damage, int maxDurability) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return itemStack;
        if (meta instanceof Damageable damageable) {
            applyCustomDurabilityBar(damageable, damage, maxDurability);
            itemStack.setItemMeta(meta);
        } else {
            itemStack = applyCustomDurabilityBarSpecial(itemStack, damage, maxDurability);
        }
        return itemStack;
    }

    void applyCustomDurabilityBar(@Nonnull Damageable itemMeta, int damage, int maxDurability);

    @Nonnull
    ItemStack applyCustomDurabilityBarSpecial(@Nonnull ItemStack itemStack, int damage, int maxDurability);

    @Nonnull
    default ItemStack restoreCustomDurabilityBar(@Nonnull ItemStack itemStack) {
        if (isNeedSpecialRestore(itemStack))
            return restoreCustomDurabilityBarSpecial(itemStack);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return itemStack;
        if (meta instanceof Damageable damageable) {
            restoreCustomDurabilityBar(damageable, itemStack.getType().getMaxDurability());
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    void restoreCustomDurabilityBar(@Nonnull Damageable itemMeta, int maxDurability);

    @Nonnull
    ItemStack restoreCustomDurabilityBarSpecial(@Nonnull ItemStack itemStack);

    boolean isNeedSpecialRestore(@Nonnull ItemStack itemStack);
}
