package org.ricetea.barleyteaapi.api.item.registration;

import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.base.registration.NSKeyedRegister;
import org.ricetea.barleyteaapi.api.item.render.ItemSubRenderer;
import org.ricetea.barleyteaapi.internal.item.registration.ItemSubRendererRegisterImpl;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
public interface ItemSubRendererRegister extends NSKeyedRegister<ItemSubRenderer> {
    @Nonnull
    static ItemSubRendererRegister getInstance() {
        return ItemSubRendererRegisterImpl.getInstance();
    }

    @Nullable
    static ItemSubRendererRegister getInstanceUnsafe() {
        return ItemSubRendererRegisterImpl.getInstanceUnsafe();
    }

    static boolean hasRegistered() {
        return !ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), IRegister::isEmpty), true);
    }
}
