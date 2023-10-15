package org.ricetea.barleyteaapi.api.block.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.ricetea.barleyteaapi.api.block.BaseBlock;
import org.ricetea.utils.Either;

public final class DataBlockType extends Either<Material, BaseBlock> {

    @Nonnull
    private static final DataBlockType EMPTY = create(Material.AIR);

    @Nonnull
    public static DataBlockType empty() {
        return EMPTY;
    }

    private DataBlockType(Material left, BaseBlock right) {
        super(left, right);
    }

    @Nonnull
    public static DataBlockType create(Material type) {
        return new DataBlockType(type, null);
    }

    @Nonnull
    public static DataBlockType create(BaseBlock type) {
        return new DataBlockType(null, type);
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
    public Material getItemTypeForMinecraftBuiltInBlock() {
        return left();
    }

    @Nullable
    public BaseBlock getItemTypeForBarleyTeaCustomBlock() {
        return right();
    }

    @Nonnull
    public Material toMaterial() {
        Material left = left();
        if (left != null)
            return left;
        BaseBlock right = right();
        if (right != null)
            return right.getBlockTypeBasedOn();
        return Material.AIR;
    }
}
