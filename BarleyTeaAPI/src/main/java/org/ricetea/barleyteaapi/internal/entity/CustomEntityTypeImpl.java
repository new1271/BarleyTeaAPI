package org.ricetea.barleyteaapi.internal.entity;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.utils.Either;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Immutable
@ApiStatus.Internal
public class CustomEntityTypeImpl extends Either<EntityType, CustomEntity> implements CustomEntityType {
    @Nonnull
    private static final ConcurrentHashMap<EntityType, CustomEntityType> vanillaEntityTypeMap = new ConcurrentHashMap<>();
    @Nonnull
    private static final ConcurrentHashMap<CustomEntity, CustomEntityType> customEntityMap = new ConcurrentHashMap<>();

    private CustomEntityTypeImpl(@Nullable EntityType left) {
        super(left, null);
    }

    private CustomEntityTypeImpl(@Nullable CustomEntity right) {
        super(null, right);
    }

    @Nonnull
    public static CustomEntityType get(@Nonnull EntityType entityType) {
        return vanillaEntityTypeMap.computeIfAbsent(entityType, CustomEntityTypeImpl::new);
    }

    @Nonnull
    public static CustomEntityType get(@Nonnull CustomEntity customEntity) {
        return customEntityMap.computeIfAbsent(customEntity, CustomEntityTypeImpl::new);
    }

    @Nonnull
    public static CustomEntityType get(@Nonnull NamespacedKey key) {
        EntityRegister register = EntityRegister.getInstanceUnsafe();
        if (register != null) {
            CustomEntity entityType = register.lookup(key);
            if (entityType != null)
                return get(entityType);
        }
        Optional<EntityType> entityTypeOptional = Arrays.stream(EntityType.values())
                .filter(material -> material.getKey().equals(key))
                .findAny();
        return entityTypeOptional.map(CustomEntityType::get).orElseGet(CustomEntityType::unknown);
    }

    @Nonnull
    public static CustomEntityType get(@Nonnull Entity entity) {
        CustomEntity entityType = CustomEntity.get(entity);
        return entityType == null ? get(entity.getType()) : get(entityType);
    }

    @ApiStatus.Internal
    public static void removeInstances(@Nonnull Collection<CustomEntity> entities) {
        entities.forEach(customEntityMap::remove);
    }

    @Nullable
    @Override
    public EntityType asEntityType() {
        return left();
    }

    @Nullable
    @Override
    public CustomEntity asCustomEntity() {
        return right();
    }
}
