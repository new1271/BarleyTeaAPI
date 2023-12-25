package org.ricetea.barleyteaapi.api.entity.registration;

import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.internal.entity.registration.EntityRegisterImpl;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface EntityRegister extends IRegister<CustomEntity> {
    @Nonnull
    static EntityRegister getInstance() {
        return Objects.requireNonNull(getInstanceUnsafe());
    }

    @Nullable
    static EntityRegister getInstanceUnsafe() {
        return EntityRegisterImpl.getInstanceUnsafe();
    }

    static boolean hasRegistered() {
        return ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), EntityRegister::hasAnyRegistered), false);
    }
}
