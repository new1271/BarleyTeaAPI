package org.ricetea.barleyteaapi.api.entity.counter;

import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface TickCounterTrigger {
    boolean trigger(@Nonnull TickCounter counter, @Nonnull Entity affectedEntity, int count);
}
