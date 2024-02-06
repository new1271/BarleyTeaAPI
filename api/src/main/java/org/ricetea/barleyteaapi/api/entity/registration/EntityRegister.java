package org.ricetea.barleyteaapi.api.entity.registration;

import org.bukkit.Bukkit;
import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.base.registration.NSKeyedRegister;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Objects;

@Singleton
public interface EntityRegister extends NSKeyedRegister<CustomEntity> {
    @Nonnull
    static EntityRegister getInstance() {
        return Objects.requireNonNull(getInstanceUnsafe());
    }

    @Nullable
    static EntityRegister getInstanceUnsafe() {
        return Bukkit.getServicesManager().load(EntityRegister.class);
    }

    static boolean hasRegistered() {
        return !ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), IRegister::isEmpty), true);
    }
}
