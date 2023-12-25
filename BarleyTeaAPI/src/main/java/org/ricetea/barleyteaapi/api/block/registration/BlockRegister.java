package org.ricetea.barleyteaapi.api.block.registration;

import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.barleyteaapi.internal.block.registration.BlockRegisterImpl;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface BlockRegister extends IRegister<CustomBlock> {
    @Nonnull
    static BlockRegister getInstance() {
        return Objects.requireNonNull(getInstanceUnsafe());
    }

    @Nullable
    static BlockRegister getInstanceUnsafe() {
        BarleyTeaAPI.checkPluginUsable();
        return BlockRegisterImpl.getInstanceUnsafe();
    }

    static boolean hasRegistered() {
        return ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), BlockRegister::hasAnyRegistered), false);
    }
}
