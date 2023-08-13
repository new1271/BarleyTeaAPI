package org.ricetea.barleyteaapi.api.item.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.util.Either;

public final class DataItemType extends Either<Material, BaseItem> {

    @Nonnull
    private static final DataItemType EMPTY = create(Material.AIR);

    @Nonnull
    public static DataItemType empty() {
        return EMPTY;
    }

    private DataItemType(Material left, BaseItem right) {
        super(left, right);
    }

    @Nonnull
    public static DataItemType create(Material type) {
        return new DataItemType(type, null);
    }

    @Nonnull
    public static DataItemType create(BaseItem type) {
        return new DataItemType(null, type);
    }

    public boolean isMinecraftBuiltInItem() {
        return isLeft();
    }

    public boolean isBarleyTeaCustomItem() {
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
    public Material getItemTypeForMinecraftBuiltInItem() {
        return left();
    }

    @Nullable
    public BaseItem getItemTypeForBarleyTeaCustomItem() {
        return right();
    }
}
