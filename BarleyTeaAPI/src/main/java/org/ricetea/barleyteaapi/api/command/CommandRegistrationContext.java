package org.ricetea.barleyteaapi.api.command;

import javax.annotation.Nonnull;

public abstract class CommandRegistrationContext<T> {

    public abstract @Nonnull T getContext();
}
