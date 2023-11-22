package org.ricetea.barleyteaapi.api.item.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Either;
import org.ricetea.utils.ObjectUtil;

public final class DataItemType extends Either<Material, BaseItem> implements Keyed {

    @Nonnull
    private static final DataItemType EMPTY = create(Material.AIR);

    @Nonnull
    private static final HashMap<Material, DataItemType> vanillaMaterialMap = new HashMap<>();

    @Nonnull
    public static DataItemType empty() {
        return EMPTY;
    }

    private DataItemType(@Nullable Material left, @Nullable BaseItem right) {
        super(left, right);
    }

    @Nonnull
    public static DataItemType get(@Nullable Material itemType) {
        if (itemType == null)
            return empty();
        else
            return Objects.requireNonNull(vanillaMaterialMap.computeIfAbsent(itemType, DataItemType::create));
    }

    @Nonnull
    public static DataItemType get(@Nullable BaseItem itemType) {
        if (itemType == null)
            return empty();
        else
            return itemType.getType();
    }

    @Nonnull
    public static DataItemType get(@Nullable NamespacedKey key) {
        if (key == null || NamespacedKeyUtil.empty().equals(key))
            return empty();
        else {
            ItemRegister register = ItemRegister.getInstanceUnsafe();
            if (register != null) {
                BaseItem itemType = register.lookup(key);
                if (itemType != null)
                    return get(itemType);
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
    public static DataItemType get(@Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir())
            return empty();
        else {
            ItemRegister register = ItemRegister.getInstanceUnsafe();
            if (register != null) {
                BaseItem itemType = register.lookup(BaseItem.getItemID(itemStack));
                if (itemType != null)
                    return get(itemType);
            }
            return get(itemStack.getType());
        }
    }

    //This method should be used internally
    @Deprecated
    @Nonnull
    public static DataItemType create(@Nonnull BaseItem itemType) {
        return new DataItemType(null, itemType);
    }

    //This method should be used internally
    @Deprecated
    @Nonnull
    private static DataItemType create(Material type) {
        return new DataItemType(type, null);
    }

    @Nonnull
    @Override
    public NamespacedKey getKey() {
        return ObjectUtil.letNonNull(
                mapLeftOrRight(Material::getKey, BaseItem::getKey),
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
    public BaseItem asCustomItem() {
        return right();
    }

    @Nonnull
    public Material getMaterialBasedOn() {
        Material left = left();
        if (left != null)
            return left;
        BaseItem right = right();
        if (right != null)
            return right.getMaterialBasedOn();
        return Material.AIR;
    }
}
