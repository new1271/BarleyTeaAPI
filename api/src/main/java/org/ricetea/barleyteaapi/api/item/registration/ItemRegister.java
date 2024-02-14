package org.ricetea.barleyteaapi.api.item.registration;

import org.bukkit.Bukkit;
import org.ricetea.barleyteaapi.api.base.registration.CustomObjectRegister;
import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.feature.ItemFeature;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Objects;

@Singleton
public interface ItemRegister extends CustomObjectRegister<CustomItem, ItemFeature> {

    @Nonnull
    static ItemRegister getInstance() {
        return Objects.requireNonNull(getInstanceUnsafe());
    }

    @Nullable
    static ItemRegister getInstanceUnsafe() {
        return Bukkit.getServicesManager().load(ItemRegister.class);
    }

    static boolean hasRegistered() {
        return !ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), IRegister::isEmpty), true);
    }
}
