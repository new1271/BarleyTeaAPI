package org.ricetea.barleyteaapi.api.entity.counter;

import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface SimpleTickCounterTrigger extends TickCounterTrigger {
    boolean trigger(@Nonnull Entity entity, int tick);

    default void trigger(@Nonnull TickCounterTriggerData data) {
        if (trigger(data.getEntity(), data.getTickCount())) {
            data.resetTickCount();
        }
    }
}
