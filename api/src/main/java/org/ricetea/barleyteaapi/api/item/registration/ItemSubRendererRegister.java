package org.ricetea.barleyteaapi.api.item.registration;

import org.bukkit.Bukkit;
import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.base.registration.NSKeyedRegister;
import org.ricetea.barleyteaapi.api.item.render.ItemSubRenderer;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Objects;

@Singleton
public interface ItemSubRendererRegister extends NSKeyedRegister<ItemSubRenderer> {

    @Nonnull
    static ItemSubRendererRegister getInstance() {
        return Objects.requireNonNull(getInstanceUnsafe());
    }

    @Nullable
    static ItemSubRendererRegister getInstanceUnsafe() {
        return Bukkit.getServicesManager().load(ItemSubRendererRegister.class);
    }

    static boolean hasRegistered() {
        return !ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), IRegister::isEmpty), true);
    }
}
