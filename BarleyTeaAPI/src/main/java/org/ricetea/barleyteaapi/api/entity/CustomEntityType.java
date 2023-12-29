package org.ricetea.barleyteaapi.api.entity;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Unmodifiable;
import org.ricetea.barleyteaapi.internal.entity.CustomEntityTypeImpl;
import org.ricetea.barleyteaapi.internal.entity.UnknownCustomEntityTypeImpl;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.EitherOperation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

@Unmodifiable
public interface CustomEntityType extends EitherOperation<EntityType, CustomEntity>, Keyed {
    @Nonnull
    static CustomEntityType unknown() {
        return UnknownCustomEntityTypeImpl.getInstance();
    }

    @Nonnull
    static CustomEntityType get(@Nullable EntityType entityType) {
        if (entityType == null)
            return unknown();
        return CustomEntityTypeImpl.get(entityType);
    }

    @Nonnull
    static CustomEntityType get(@Nullable CustomEntity customEntity) {
        if (customEntity == null)
            return unknown();
        return CustomEntityTypeImpl.get(customEntity);
    }

    @Nonnull
    static CustomEntityType get(@Nullable NamespacedKey key) {
        if (key == null || NamespacedKeyUtil.empty().equals(key))
            return unknown();
        else {
            return CustomEntityTypeImpl.get(key);
        }
    }

    @Nonnull
    static CustomEntityType get(@Nullable Entity entity) {
        if (entity == null)
            return unknown();
        else {
            return CustomEntityTypeImpl.get(entity);
        }
    }

    @Nullable
    EntityType asEntityType();

    @Nullable
    CustomEntity asCustomEntity();

    boolean isUnknown();

    default boolean isEntityType() {
        return asEntityType() != null;
    }

    default boolean isCustomEntity() {
        return asCustomEntity() != null;
    }

    @Nonnull
    default EntityType getOriginalType() {
        return nonNullMap(Function.identity(), CustomEntity::getOriginalType);
    }

    @Nonnull
    default NamespacedKey getKey() {
        return nonNullMap(EntityType::getKey, CustomEntity::getKey);
    }
}
