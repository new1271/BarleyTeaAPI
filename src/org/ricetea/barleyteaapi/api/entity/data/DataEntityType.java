package org.ricetea.barleyteaapi.api.entity.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.EntityType;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.util.Either;

public final class DataEntityType extends Either<EntityType, BaseEntity> {

    private DataEntityType(EntityType left, BaseEntity right) {
        super(left, right);
    }

    @Nonnull
    public static DataEntityType create(EntityType type) {
        return new DataEntityType(type, null);
    }

    @Nonnull
    public static DataEntityType create(BaseEntity type) {
        return new DataEntityType(null, type);
    }

    public boolean isMinecraftBuiltInMob() {
        return isLeft();
    }

    public boolean isBarleyTeaCustomMob() {
        return isRight();
    }

    public boolean isPlayer() {
        EntityType type = left();
        if (type == null)
            return false;
        return type.equals(EntityType.PLAYER);
    }

    @Nullable
    public EntityType getEntityTypeForMinecraftBuiltInMob() {
        return left();
    }

    @Nullable
    public BaseEntity getEntityTypeForBarleyTeaCustomMob() {
        return right();
    }
}
