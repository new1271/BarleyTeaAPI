package org.ricetea.barleyteaapi.api.block.registration;

import org.bukkit.Bukkit;
import org.ricetea.barleyteaapi.api.base.registration.CustomObjectRegister;
import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.barleyteaapi.api.block.feature.BlockFeature;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Objects;

@Singleton
public interface BlockRegister extends CustomObjectRegister<CustomBlock, BlockFeature> {
    @Nonnull
    static BlockRegister getInstance() {
        return Objects.requireNonNull(getInstanceUnsafe());
    }

    @Nullable
    static BlockRegister getInstanceUnsafe() {
        return Bukkit.getServicesManager().load(BlockRegister.class);
    }

    static boolean hasRegistered() {
        return !ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), IRegister::isEmpty), true);
    }
}
