package org.ricetea.barleyteaapi.api.command;

import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.base.registration.IRegister;
import org.ricetea.barleyteaapi.internal.command.CommandRegisterBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@ApiStatus.Experimental
public interface CommandRegister<T> extends IRegister<Command<T>> {

    @Nonnull
    static <C, T extends CommandRegister<C>> T getInstance(@Nonnull Class<T> clazz) {
        return Objects.requireNonNull(getInstanceUnsafe(clazz));
    }

    @Nullable
    static <C, T extends CommandRegister<C>> T getInstanceUnsafe(@Nonnull Class<T> clazz) {
        return CommandRegisterBase.getInstanceUnsafe(clazz);
    }

    static <C, T extends CommandRegister<C>> void setInstance(@Nullable T register, @Nonnull Class<T> clazz) {
        CommandRegisterBase.setInstance(register, clazz);
    }
}
