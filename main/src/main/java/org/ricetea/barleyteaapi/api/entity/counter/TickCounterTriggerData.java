package org.ricetea.barleyteaapi.api.entity.counter;

import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import java.util.function.IntUnaryOperator;

public final class TickCounterTriggerData {
    @Nonnull
    private final TickCounter counter;

    @Nonnull
    private final Entity entity;

    private final int countBefore;

    private int count;

    public TickCounterTriggerData(@Nonnull TickCounter counter, @Nonnull Entity entity, int count) {
        this(counter, entity, count, counter.getOperator().applyAsInt(count));
    }

    public TickCounterTriggerData(@Nonnull TickCounter counter, @Nonnull Entity entity, @Nonnull IntUnaryOperator increaseFunction, int count) {
        this(counter, entity, count, increaseFunction.applyAsInt(count));
    }

    public TickCounterTriggerData(@Nonnull TickCounter counter, @Nonnull Entity entity, int countBefore, int count) {
        this.counter = counter;
        this.entity = entity;
        this.count = count;
        this.countBefore = countBefore;
    }

    @Nonnull
    public TickCounter getTickCounter() {
        return counter;
    }

    @Nonnull
    public Entity getEntity() {
        return entity;
    }

    public int getTickCountBeforeTrigger() {
        return countBefore;
    }

    public int getTickCount() {
        return count;
    }

    public void setTickCount(int count) {
        this.count = count;
    }

    public void resetTickCount() {
        this.count = counter.getStartValue();
    }

    public void setTickCountAsBeforeTrigger() {
        this.count = countBefore;
    }
}
