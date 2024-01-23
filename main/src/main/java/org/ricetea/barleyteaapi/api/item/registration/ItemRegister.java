package org.ricetea.barleyteaapi.api.item.registration;

import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.base.registration.NSKeyedRegister;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.internal.item.registration.ItemRegisterImpl;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
public interface ItemRegister extends NSKeyedRegister<CustomItem> {
    @Nonnull
    static ItemRegister getInstance() {
        return ItemRegisterImpl.getInstance();
    }

    @Nullable
    static ItemRegister getInstanceUnsafe() {
        return ItemRegisterImpl.getInstanceUnsafe();
    }

    static boolean hasRegistered() {
        return !ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), IRegister::isEmpty), true);
    }
}
