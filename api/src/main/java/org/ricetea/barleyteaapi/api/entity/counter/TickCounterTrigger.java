package org.ricetea.barleyteaapi.api.entity.counter;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface TickCounterTrigger {
    @Nonnull
    static TickCounterTrigger simpleTrigger(@Nonnull SimpleTickCounterTrigger trigger) {
        return trigger;
    }

    void trigger(@Nonnull TickCounterTriggerData data);
}
