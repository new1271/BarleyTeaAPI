package org.ricetea.barleyteaapi.api.localization;

import org.bukkit.Bukkit;
import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.api.base.registration.StringKeyedRegister;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface LocalizationRegister extends StringKeyedRegister<LocalizedMessageFormat> {

    @Nonnull
    static LocalizationRegister getInstance() {
        return Objects.requireNonNull(getInstanceUnsafe());
    }

    @Nullable
    static LocalizationRegister getInstanceUnsafe() {
        return Bukkit.getServicesManager().load(LocalizationRegister.class);
    }

    static boolean hasRegistered() {
        return !ObjectUtil.letNonNull(ObjectUtil.safeMap(getInstanceUnsafe(), IRegister::isEmpty), true);
    }

    @Override
    default boolean hasRegistered(@Nullable LocalizedMessageFormat string) { //Replaced to better implementation
        if (string == null)
            return false;
        return lookup(string.getTranslationKey()) == string;
    }
}
