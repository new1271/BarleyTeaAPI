package org.ricetea.barleyteaapi.api.entity.data;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Either;
import org.ricetea.utils.ObjectUtil;

public final class DataEntityType extends Either<EntityType, BaseEntity> implements Keyed {

    @Nonnull
    private static final DataEntityType EMPTY = create(EntityType.UNKNOWN);

    @Nonnull
    private static final Hashtable<EntityType, DataEntityType> vanillaEntityTypeMap = new Hashtable<>();

    @Nonnull
    public static DataEntityType empty() {
        return EMPTY;
    }

    private DataEntityType(@Nullable EntityType left, @Nullable BaseEntity right) {
        super(left, right);
    }

    @Nonnull
    public static DataEntityType get(@Nullable EntityType entityType) {
        if (entityType == null)
            return empty();
        else
            return Objects.requireNonNull(vanillaEntityTypeMap.computeIfAbsent(entityType, DataEntityType::create));
    }

    @Nonnull
    public static DataEntityType get(@Nullable BaseEntity entityType) {
        if (entityType == null)
            return empty();
        else
            return entityType.getType();
    }

    @Nonnull
    public static DataEntityType get(@Nullable NamespacedKey key) {
        if (key == null || NamespacedKeyUtil.empty().equals(key))
            return empty();
        else {
            EntityRegister register = EntityRegister.getInstanceUnsafe();
            if (register != null) {
                BaseEntity entityType = register.lookup(key);
                if (entityType != null)
                    return get(entityType);
            }
            Optional<EntityType> entityTypeOptional = Arrays.stream(EntityType.values())
                    .filter(entityType -> entityType.getKey().equals(key))
                    .findAny();
            if (entityTypeOptional.isPresent()) {
                return get(entityTypeOptional.get());
            }
            return empty();
        }
    }

    @Nonnull
    public static DataEntityType get(@Nullable Entity entity) {
        if (entity == null || entity.getType().equals(EntityType.UNKNOWN))
            return empty();
        else {
            EntityRegister register = EntityRegister.getInstanceUnsafe();
            if (register != null) {
                BaseEntity entityType = register.lookup(BaseEntity.getEntityID(entity));
                if (entityType != null)
                    return get(entityType);
            }
            return get(entity.getType());
        }
    }

    @Nonnull
    @Override
    public NamespacedKey getKey() {
        return ObjectUtil.letNonNull(
                map(EntityType::getKey, BaseEntity::getKey),
                NamespacedKeyUtil::empty);
    }

    //This method should be used internally
    @Deprecated
    @Nonnull
    public static DataEntityType create(@Nonnull BaseEntity itemType) {
        return new DataEntityType(null, itemType);
    }

    //This method should be used internally
    @Deprecated
    @Nonnull
    private static DataEntityType create(@Nonnull EntityType type) {
        return new DataEntityType(type, null);
    }

    public boolean isVanilla() {
        return isLeft();
    }

    public boolean isCustom() {
        return isRight();
    }

    public boolean isPlayer() {
        EntityType type = left();
        if (type == null)
            return false;
        return type.equals(EntityType.PLAYER);
    }

    public boolean isUnknown() {
        if (this == EMPTY) {
            return true;
        } else {
            EntityType left = left();
            if (left != null) {
                return left.equals(EntityType.UNKNOWN);
            }
            return right() == null;
        }
    }

    public boolean isEmpty() {
        return isUnknown();
    }

    @Nullable
    public EntityType asEntityType() {
        return left();
    }

    @Nullable
    public BaseEntity asCustomEntity() {
        return right();
    }

    @Nonnull
    public EntityType getEntityTypeBasedOn() {
        EntityType left = left();
        if (left != null)
            return left;
        BaseEntity right = right();
        if (right != null)
            return right.getEntityTypeBasedOn();
        return EntityType.UNKNOWN;
    }
}
