package org.ricetea.barleyteaapi.api.command;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;

@ApiStatus.Experimental
public interface Command<T> extends Keyed {
    void register(@Nonnull CommandRegistrationContext<T> registrationContext);

    void unregister(@Nonnull CommandRegistrationContext<T> registrationContext);

    @Nonnull
    NamespacedKey[] getAliases();
}
