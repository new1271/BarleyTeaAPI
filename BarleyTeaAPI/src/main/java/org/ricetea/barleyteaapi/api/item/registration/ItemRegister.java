package org.ricetea.barleyteaapi.api.item.registration;

import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.internal.item.registration.ItemRegisterImpl;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ItemRegister extends IRegister<CustomItem> {
    @Nonnull
    static ItemRegister getInstance() {
        BarleyTeaAPI.checkPluginUsable();
        return ItemRegisterImpl.getInstance();
    }

    @Nullable
    static ItemRegister getInstanceUnsafe() {
        BarleyTeaAPI.checkPluginUsable();
        return ItemRegisterImpl.getInstanceUnsafe();
    }

    static boolean hasRegistered() {
        return ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), ItemRegister::hasAnyRegistered), false);
    }

    static boolean hasRegisteredNeedMovingFeature() {
        return ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), ItemRegister::hasAnyRegisteredNeedMovingFeature), false);
    }

    static boolean hasRegisteredNeedTicking() {
        return ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), ItemRegister::hasAnyRegisteredNeedTicking), false);
    }

    boolean hasAnyRegisteredNeedMovingFeature();

    boolean hasAnyRegisteredNeedTicking();
}
