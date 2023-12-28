package org.ricetea.barleyteaapi.api.command;

import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;

@ApiStatus.Experimental
public interface CommandRegistrationContext<T> {

    @Nonnull
    T getContext();
}
