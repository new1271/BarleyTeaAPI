package org.ricetea.barleyteaapi.api.entity.registration;

import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.internal.entity.registration.EntityRegisterImpl;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
public interface EntityRegister extends IRegister<CustomEntity> {
    @Nonnull
    static EntityRegister getInstance() {
        BarleyTeaAPI.checkPluginUsable();
        return EntityRegisterImpl.getInstance();
    }

    @Nullable
    static EntityRegister getInstanceUnsafe() {
        BarleyTeaAPI.checkPluginUsable();
        return EntityRegisterImpl.getInstanceUnsafe();
    }

    static boolean hasRegistered() {
        return ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), EntityRegister::hasAnyRegistered), false);
    }
}
