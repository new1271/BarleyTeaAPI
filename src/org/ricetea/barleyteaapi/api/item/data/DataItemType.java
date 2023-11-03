package org.ricetea.barleyteaapi.api.item.data;

import java.util.HashMap;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.utils.Either;

public final class DataItemType extends Either<Material, BaseItem> {

    @Nonnull
    private static final DataItemType EMPTY = get0(Material.AIR);

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
            return Objects.requireNonNull(vanillaMaterialMap.computeIfAbsent(itemType, DataItemType::get0));
    }

    @Nonnull
    public static DataItemType get(@Nullable BaseItem itemType) {
        if (itemType == null)
            return empty();
        else
            return itemType.getType();
    }

    //This method should be internally
    @Deprecated
    @Nonnull
    public static DataItemType create(@Nonnull BaseItem itemType) {
        return new DataItemType(null, itemType);
    }

    @Nonnull
    private static DataItemType get0(Material type) {
        return new DataItemType(type, null);
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
