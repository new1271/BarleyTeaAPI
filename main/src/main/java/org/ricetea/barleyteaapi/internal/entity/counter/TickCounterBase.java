package org.ricetea.barleyteaapi.internal.entity.counter;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.counter.TickCounter;
import org.ricetea.barleyteaapi.api.entity.counter.TickCounterTrigger;
import org.ricetea.barleyteaapi.api.entity.counter.TickCounterTriggerData;

import javax.annotation.Nonnull;
import java.util.function.IntUnaryOperator;

@ApiStatus.Internal
public abstract class TickCounterBase implements TickCounter {

    @Nonnull
    private final NamespacedKey key;

    @Nonnull
    private final IntUnaryOperator operator;

    @Nonnull
    private final TickCounterTrigger trigger;

    private final int startValue;

    public TickCounterBase(@Nonnull NamespacedKey key, @Nonnull IntUnaryOperator operator,
                           @Nonnull TickCounterTrigger trigger, int startValue) {
        this.key = key;
        this.operator = operator;
        this.trigger = trigger;
        this.startValue = startValue;
    }

    @Nonnull
    @Override
    public final NamespacedKey getKey() {
        return key;
    }

    @Nonnull
    @Override
    public final IntUnaryOperator getOperator() {
        return operator;
    }

    @Nonnull
    @Override
    public final TickCounterTrigger getTrigger() {
        return trigger;
    }

    @Override
    public final int getStartValue() {
        return startValue;
    }

    public final void tick(@Nonnull Entity entity) {
        int count = getCounter(entity);
        TickCounterTriggerData data = new TickCounterTriggerData(this, entity, count);
        trigger.trigger(data);
        int newCount = data.getTickCount();
        if (count != newCount) {
            setCounter(entity, newCount);
        }
    }
}
