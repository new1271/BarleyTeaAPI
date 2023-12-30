package org.ricetea.barleyteaapi.api.item;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.internal.item.CustomItemTypeImpl;
import org.ricetea.barleyteaapi.internal.item.EmptyCustomItemTypeImpl;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.EitherOperation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public interface CustomItemType extends EitherOperation<Material, CustomItem>, Keyed {

    @Nonnull
    static CustomItemType empty() {
        return EmptyCustomItemTypeImpl.getInstance();
    }

    @Nonnull
    static CustomItemType get(@Nullable Material material) {
        if (material == null)
            return empty();
        return CustomItemTypeImpl.get(material);
    }

    @Nonnull
    static CustomItemType get(@Nullable CustomItem customItem) {
        if (customItem == null)
            return empty();
        return CustomItemTypeImpl.get(customItem);
    }

    @Nonnull
    static CustomItemType get(@Nullable NamespacedKey key) {
        if (key == null || NamespacedKeyUtil.empty().equals(key))
            return empty();
        else {
            return CustomItemTypeImpl.get(key);
        }
    }

    @Nonnull
    static CustomItemType get(@Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty())
            return empty();
        else {
            return CustomItemTypeImpl.get(itemStack);
        }
    }

    @Nullable
    Material asMaterial();

    @Nullable
    CustomItem asCustomItem();

    boolean isEmpty();

    default boolean isMaterial() {
        return asMaterial() != null;
    }

    default boolean isCustomItem() {
        return asCustomItem() != null;
    }

    @Nonnull
    default Material getOriginalType() {
        return nonNullMap(Function.identity(), CustomItem::getOriginalType);
    }

    @Nonnull
    default NamespacedKey getKey() {
        return nonNullMap(Material::getKey, CustomItem::getKey);
    }
}
