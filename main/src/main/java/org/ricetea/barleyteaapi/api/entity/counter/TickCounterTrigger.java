package org.ricetea.barleyteaapi.api.entity.counter;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface TickCounterTrigger {
    void trigger(@Nonnull TickCounterTriggerData data);

    @Nonnull
    static TickCounterTrigger simpleTrigger(@Nonnull SimpleTickCounterTrigger trigger) {
        return trigger;
    }
}
