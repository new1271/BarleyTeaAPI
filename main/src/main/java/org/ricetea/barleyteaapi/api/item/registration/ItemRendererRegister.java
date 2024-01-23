package org.ricetea.barleyteaapi.api.item.registration;

import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.base.registration.NSKeyedRegister;
import org.ricetea.barleyteaapi.api.item.render.ItemRenderer;
import org.ricetea.barleyteaapi.internal.item.registration.ItemRendererRegisterImpl;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
public interface ItemRendererRegister extends NSKeyedRegister<ItemRenderer> {
    @Nonnull
    static ItemRendererRegister getInstance() {
        BarleyTeaAPI.checkPluginUsable();
        return ItemRendererRegisterImpl.getInstance();
    }

    @Nullable
    static ItemRendererRegister getInstanceUnsafe() {
        BarleyTeaAPI.checkPluginUsable();
        return ItemRendererRegisterImpl.getInstanceUnsafe();
    }

    static boolean hasRegistered() {
        return !ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), IRegister::isEmpty), true);
    }
}
