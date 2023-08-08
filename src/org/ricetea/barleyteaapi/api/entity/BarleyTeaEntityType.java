package org.ricetea.barleyteaapi.api.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.EntityType;
import org.ricetea.barleyteaapi.util.Either;

public final class BarleyTeaEntityType extends Either<EntityType, BaseEntity> {

    private BarleyTeaEntityType(EntityType left, BaseEntity right) {
        super(left, right);
    }

    @Nonnull
    public static BarleyTeaEntityType create(EntityType type) {
        return new BarleyTeaEntityType(type, null);
    }

    @Nonnull
    public static BarleyTeaEntityType create(BaseEntity type) {
        return new BarleyTeaEntityType(null, type);
    }

    public boolean isMinecraftBuiltInMob() {
        return isLeft();
    }

    public boolean isBarleyTeaCustomMob() {
        return isRight();
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
