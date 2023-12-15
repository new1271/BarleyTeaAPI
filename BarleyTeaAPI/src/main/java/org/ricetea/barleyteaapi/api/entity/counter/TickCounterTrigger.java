package org.ricetea.barleyteaapi.api.entity.counter;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;

@FunctionalInterface
public interface TickCounterTrigger {
    boolean triggerAndReturnNeedClean(@Nonnull Entity affectedEntity, int count);
}
