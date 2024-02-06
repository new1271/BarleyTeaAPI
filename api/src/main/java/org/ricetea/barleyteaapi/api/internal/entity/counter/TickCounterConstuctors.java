package org.ricetea.barleyteaapi.api.internal.entity.counter;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.ricetea.barleyteaapi.api.entity.counter.TickCounter;
import org.ricetea.barleyteaapi.api.entity.counter.TickCounterTrigger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.IntUnaryOperator;

public interface TickCounterConstuctors {

    @Nonnull
    static TickCounterConstuctors getInstance() {
        return Objects.requireNonNull(getInstanceUnsafe());
    }

    @Nullable
    static TickCounterConstuctors getInstanceUnsafe() {
        return Bukkit.getServicesManager().load(TickCounterConstuctors.class);
    }

    @Nonnull
    TickCounter persistentCounter(@Nonnull NamespacedKey key, @Nonnull IntUnaryOperator operator,
                                  @Nonnull TickCounterTrigger trigger, int startValue);

    @Nonnull
    TickCounter transistentCounter(@Nonnull NamespacedKey key, @Nonnull IntUnaryOperator operator,
                                   @Nonnull TickCounterTrigger trigger, int startValue);
}
