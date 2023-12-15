package org.ricetea.barleyteaapi.api.block.data;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.ricetea.barleyteaapi.api.block.BaseBlock;
import org.ricetea.barleyteaapi.api.block.registration.BlockRegister;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Either;
import org.ricetea.utils.ObjectUtil;

public final class DataBlockType extends Either<Material, BaseBlock> implements Keyed {

    @Nonnull
    private static final DataBlockType EMPTY = create(Material.AIR);

    @Nonnull
    private static final Hashtable<Material, DataBlockType> vanillaMaterialMap = new Hashtable<>();

    @Nonnull
    public static DataBlockType empty() {
        return EMPTY;
    }

    private DataBlockType(@Nullable Material left, @Nullable BaseBlock right) {
        super(left, right);
    }

    @Nonnull
    public static DataBlockType get(@Nullable Material itemType) {
        if (itemType == null)
            return empty();
        else
            return Objects.requireNonNull(vanillaMaterialMap.computeIfAbsent(itemType, DataBlockType::create));
    }

    @Nonnull
    public static DataBlockType get(@Nullable BaseBlock blockType) {
        if (blockType == null)
            return empty();
        else
            return blockType.getType();
    }

    @Nonnull
    public static DataBlockType get(@Nullable NamespacedKey key) {
        if (key == null || NamespacedKeyUtil.empty().equals(key))
            return empty();
        else {
            BlockRegister register = BlockRegister.getInstanceUnsafe();
            if (register != null) {
                BaseBlock blockType = register.lookup(key);
                if (blockType != null)
                    return get(blockType);
            }
            Optional<Material> materialOptional = Arrays.stream(Material.values())
                    .filter(material -> material.getKey().equals(key))
                    .findAny();
            if (materialOptional.isPresent()) {
                return get(materialOptional.get());
            }
            return empty();
        }
    }

    @Nonnull
    public static DataBlockType get(@Nullable Block block) {
        if (block == null || block.isEmpty())
            return empty();
        else {
            BlockRegister register = BlockRegister.getInstanceUnsafe();
            if (register != null) {
                BaseBlock blockType = register.lookup(BaseBlock.getBlockID(block));
                if (blockType != null)
                    return get(blockType);
            }
            return get(block.getType());
        }
    }

    //This method should be used internally
    @Deprecated
    @Nonnull
    public static DataBlockType create(@Nonnull BaseBlock blockType) {
        return new DataBlockType(null, blockType);
    }

    //This method should be used internally
    @Deprecated
    @Nonnull
    private static DataBlockType create(@Nonnull Material type) {
        return new DataBlockType(type, null);
    }

    @Nonnull
    @Override
    public NamespacedKey getKey() {
        return ObjectUtil.letNonNull(
                map(Material::getKey, BaseBlock::getKey),
                NamespacedKeyUtil::empty);
    }

    public boolean isVanilla() {
        return isLeft();
    }

    public boolean isCustom() {
        return isRight();
    }

    public boolean isAir() {
        if (this == EMPTY) {
            return true;
        } else {
            Material left = left();
            if (left != null) {
                return left.isAir();
            }
            return right() == null;
        }
    }

    @Override
    public boolean isEmpty() {
        return isAir();
    }

    @Nullable
    public Material asMaterial() {
        return left();
    }

    @Nullable
    public BaseBlock asCustomBlock() {
        return right();
    }

    @Nonnull
    public Material getBlockTypeBasedOn() {
        Material left = left();
        if (left != null)
            return left;
        BaseBlock right = right();
        if (right != null)
            return right.getBlockTypeBasedOn();
        return Material.AIR;
    }
}
