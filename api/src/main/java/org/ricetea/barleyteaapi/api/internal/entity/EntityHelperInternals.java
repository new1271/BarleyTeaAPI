package org.ricetea.barleyteaapi.api.internal.entity;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.utils.Box;
import org.ricetea.utils.WithFlag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface EntityHelperInternals {

    @Nonnull
    static EntityHelperInternals getInstance() {
        return Objects.requireNonNull(getInstanceUnsafe());
    }

    @Nullable
    static EntityHelperInternals getInstanceUnsafe() {
        return Bukkit.getServicesManager().load(EntityHelperInternals.class);
    }

    @Nullable
    Box<NamespacedKey> getCachedEntityID(@Nonnull Entity entity);

    void setCachedEntityID(@Nonnull Entity entity, @Nullable NamespacedKey key);

    void removeCachedEntity(@Nonnull Entity entity);
}
