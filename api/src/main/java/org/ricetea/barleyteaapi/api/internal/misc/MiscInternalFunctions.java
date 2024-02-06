package org.ricetea.barleyteaapi.api.internal.misc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Predicate;

public interface MiscInternalFunctions {

    @Nonnull
    static MiscInternalFunctions getInstance() {
        return Objects.requireNonNull(getInstanceUnsafe());
    }

    @Nullable
    static MiscInternalFunctions getInstanceUnsafe() {
        return Bukkit.getServicesManager().load(MiscInternalFunctions.class);
    }

    <T extends Entity> boolean tryRegisterEntityAfterSpawn(@Nonnull CustomEntity entityType, @Nonnull T entity,
                                                           @Nullable Predicate<T> predicate);

}
