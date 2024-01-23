package org.ricetea.barleyteaapi.api.entity.registration;

import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.base.registration.NSKeyedRegister;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.internal.entity.registration.EntityRegisterImpl;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
public interface EntityRegister extends NSKeyedRegister<CustomEntity> {
    @Nonnull
    static EntityRegister getInstance() {
        return EntityRegisterImpl.getInstance();
    }

    @Nullable
    static EntityRegister getInstanceUnsafe() {
        return EntityRegisterImpl.getInstanceUnsafe();
    }

    static boolean hasRegistered() {
        return !ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), IRegister::isEmpty), true);
    }
}
