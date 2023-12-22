package org.ricetea.barleyteaapi.api.entity.counter;

import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface TickCounterTrigger {
    boolean triggerAndReturnNeedClean(@Nonnull Entity affectedEntity, int count);
}
