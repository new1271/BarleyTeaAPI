package org.ricetea.barleyteaapi.api.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.ricetea.barleyteaapi.api.base.CustomObject;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CustomEntity extends CustomObject {

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
}
