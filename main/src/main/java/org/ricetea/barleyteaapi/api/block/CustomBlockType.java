package org.ricetea.barleyteaapi.api.block;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.ricetea.barleyteaapi.internal.block.CustomBlockTypeImpl;
import org.ricetea.barleyteaapi.internal.block.EmptyCustomBlockTypeImpl;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.EitherOperation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.function.Function;

@Immutable
public interface CustomBlockType extends EitherOperation<Material, CustomBlock>, Keyed {

    @Nonnull
    static CustomBlockType empty() {
        return EmptyCustomBlockTypeImpl.getInstance();
    }

    @Nonnull
    static CustomBlockType get(@Nullable Material material) {
        if (material == null)
            return empty();
        return CustomBlockTypeImpl.get(material);
    }

    @Nonnull
    static CustomBlockType get(@Nullable CustomBlock customBlock) {
        if (customBlock == null)
            return empty();
        return CustomBlockTypeImpl.get(customBlock);
    }

    @Nonnull
    static CustomBlockType get(@Nullable NamespacedKey key) {
        if (key == null || NamespacedKeyUtil.empty().equals(key))
            return empty();
        else {
            return CustomBlockTypeImpl.get(key);
        }
    }

    @Nonnull
    static CustomBlockType get(@Nullable Block block) {
        if (block == null || block.isEmpty())
            return empty();
        else {
            return CustomBlockTypeImpl.get(block);
        }
    }

    @Nullable
    Material asMaterial();

    @Nullable
    CustomBlock asCustomBlock();

    boolean isEmpty();

    default boolean isMaterial() {
        return asMaterial() != null;
    }

    default boolean isCustomBlock() {
        return asCustomBlock() != null;
    }

    @Nonnull
    default Material getOriginalType() {
        return nonNullMap(Function.identity(), CustomBlock::getOriginalType);
    }

    @Nonnull
    default NamespacedKey getKey() {
        return nonNullMap(Material::getKey, CustomBlock::getKey);
    }
}
