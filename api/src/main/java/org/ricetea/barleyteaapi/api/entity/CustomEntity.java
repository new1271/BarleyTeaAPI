package org.ricetea.barleyteaapi.api.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.ricetea.barleyteaapi.api.base.CustomObject;
import org.ricetea.barleyteaapi.api.entity.feature.EntityFeature;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public interface CustomEntity extends CustomObject<EntityFeature> {

    @Nullable
    static CustomEntity get(@Nullable Entity entity) {
        if (entity == null)
            return null;
        else {
            EntityRegister register = EntityRegister.getInstanceUnsafe();
            if (register == null)
                return null;
            return register.lookup(EntityHelper.getEntityID(entity));
        }
    }

    @Nonnull
    EntityType getOriginalType();

    @Nonnull
    default CustomEntityType getType() {
        return CustomEntityType.get(this);
    }

    @Nullable
    default <T extends EntityFeature> T getFeature(@Nonnull Class<T> featureClass) {
        return ObjectUtil.tryCast(this, featureClass);
    }

    @Nonnull
    default Collection<Class<? extends EntityFeature>> getFeatures() {
        Class<?>[] interfaces = getClass().getInterfaces();
        ArrayList<Class<? extends EntityFeature>> result = new ArrayList<>(interfaces.length);
        for (Class<?> _interface : interfaces) {
            try {
                Class<? extends EntityFeature> castedInterface = _interface.asSubclass(EntityFeature.class);
                result.add(castedInterface);
            } catch (Exception ignored) {

            }
        }
        return result.stream().collect(Collectors.toUnmodifiableSet());
    }
}
