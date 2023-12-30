package org.ricetea.barleyteaapi.api.entity.counter;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.internal.entity.counter.PersistentTickCounterImpl;
import org.ricetea.barleyteaapi.internal.entity.counter.TransientTickCounterImpl;

import javax.annotation.Nonnull;
import java.util.function.IntUnaryOperator;

public interface TickCounter extends Keyed {

    @Nonnull
    static TickCounter persistentCounter(@Nonnull NamespacedKey key, @Nonnull IntUnaryOperator operator,
                                         @Nonnull TickCounterTrigger trigger) {
        return persistentCounter(key, operator, trigger, 0);
    }

    @Nonnull
    static TickCounter persistentCounter(@Nonnull NamespacedKey key, @Nonnull IntUnaryOperator operator,
                                         @Nonnull TickCounterTrigger trigger, int startValue) {
        return new PersistentTickCounterImpl(key, operator, trigger, startValue);
    }

    @Nonnull
    static TickCounter transientCounter(@Nonnull NamespacedKey key, @Nonnull IntUnaryOperator operator,
                                        @Nonnull TickCounterTrigger trigger) {
        return transientCounter(key, operator, trigger, 0);
    }

    @Nonnull
    static TickCounter transientCounter(@Nonnull NamespacedKey key, @Nonnull IntUnaryOperator operator,
                                        @Nonnull TickCounterTrigger trigger, int startValue) {
        return new TransientTickCounterImpl(key, operator, trigger, startValue);
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
