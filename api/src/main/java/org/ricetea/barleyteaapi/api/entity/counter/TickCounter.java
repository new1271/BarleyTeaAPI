package org.ricetea.barleyteaapi.api.entity.counter;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.api.internal.entity.counter.TickCounterConstuctors;

import javax.annotation.Nonnull;
import java.util.function.IntUnaryOperator;

public interface TickCounter extends Keyed {

    @Nonnull
    static TickCounter persistentCounter(@Nonnull NamespacedKey key, @Nonnull IntUnaryOperator operator,
                                         @Nonnull SimpleTickCounterTrigger trigger) {
        return persistentCounter(key, operator, (TickCounterTrigger) trigger);
    }

    @Nonnull
    static TickCounter persistentCounter(@Nonnull NamespacedKey key, @Nonnull IntUnaryOperator operator,
                                         @Nonnull TickCounterTrigger trigger) {
        return persistentCounter(key, operator, trigger, 0);
    }

    @Nonnull
    static TickCounter persistentCounter(@Nonnull NamespacedKey key, @Nonnull IntUnaryOperator operator,
                                         @Nonnull SimpleTickCounterTrigger trigger, int startValue) {
        return persistentCounter(key, operator, (TickCounterTrigger) trigger, startValue);
    }

    @Nonnull
    static TickCounter persistentCounter(@Nonnull NamespacedKey key, @Nonnull IntUnaryOperator operator,
                                         @Nonnull TickCounterTrigger trigger, int startValue) {
        return TickCounterConstuctors.getInstance().persistentCounter(key, operator, trigger, startValue);
    }

    @Nonnull
    static TickCounter transientCounter(@Nonnull NamespacedKey key, @Nonnull IntUnaryOperator operator,
                                        @Nonnull SimpleTickCounterTrigger trigger) {
        return transientCounter(key, operator, (TickCounterTrigger) trigger);
    }

    @Nonnull
    static TickCounter transientCounter(@Nonnull NamespacedKey key, @Nonnull IntUnaryOperator operator,
                                        @Nonnull TickCounterTrigger trigger) {
        return transientCounter(key, operator, trigger, 0);
    }

    @Nonnull
    static TickCounter transientCounter(@Nonnull NamespacedKey key, @Nonnull IntUnaryOperator operator,
                                        @Nonnull SimpleTickCounterTrigger trigger, int startValue) {
        return transientCounter(key, operator, (TickCounterTrigger) trigger, startValue);
    }

    @Nonnull
    static TickCounter transientCounter(@Nonnull NamespacedKey key, @Nonnull IntUnaryOperator operator,
                                        @Nonnull TickCounterTrigger trigger, int startValue) {
        return TickCounterConstuctors.getInstance().transistentCounter(key, operator, trigger, startValue);
    }

    @Nonnull
    IntUnaryOperator getOperator();

    @Nonnull
    TickCounterTrigger getTrigger();

    int getStartValue();

    int getCounter(@Nonnull Entity entity);

    void setCounter(@Nonnull Entity entity, int count);

    default void resetCounter(@Nonnull Entity entity) {
        setCounter(entity, getStartValue());
    }

    void removeCounter(@Nonnull Entity entity);

    void tick(@Nonnull Entity entity);
}
