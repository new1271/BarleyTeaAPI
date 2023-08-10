package org.ricetea.barleyteaapi.api.item.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.util.Either;

public final class DataItemType extends Either<Material, BaseItem> {

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

    @Nullable
    public Material getItemTypeForMinecraftBuiltInItem() {
        return left();
    }

    @Nullable
    public BaseItem getItemTypeForBarleyTeaCustomItem() {
        return right();
    }
}
