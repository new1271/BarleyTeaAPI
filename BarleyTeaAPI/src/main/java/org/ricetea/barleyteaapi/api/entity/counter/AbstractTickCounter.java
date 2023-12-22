package org.ricetea.barleyteaapi.api.entity.counter;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class AbstractTickCounter implements Keyed {
    @Nonnull
    final NamespacedKey key;
    @Nullable
    final TickingOperationFunction function;
    @Nonnull
    final ArrayList<TickCounterTrigger> triggerList;

    public AbstractTickCounter(@Nonnull NamespacedKey identifierKey,
                               @Nullable TickingOperationFunction tickingCountOperationFunction, @Nullable TickCounterTrigger[] triggers) {
        key = identifierKey;
        function = tickingCountOperationFunction;
        triggerList = triggers == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(triggers));
    }

    @Nonnull
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    public final void doTick(@Nonnull Entity affectedEntity) {
        TickingOperationFunction function = this.function;
        int count = getCounter(affectedEntity);
        if (function != null) {
            count = function.doOperation(count);
            if (doTriggers(affectedEntity, count)) {
                resetCounter(affectedEntity);
            } else {
                setCounter(affectedEntity, count);
            }
        } else if (doTriggers(affectedEntity, count)) {
            resetCounter(affectedEntity);
        }
    }

    protected abstract int getCounter(@Nonnull Entity affectedEntity);

    protected abstract void setCounter(@Nonnull Entity affectedEntity, int count);

    protected abstract void resetCounter(@Nonnull Entity affectedEntity);

    public abstract void cleanCounter(@Nonnull Entity affectedEntity);

    private boolean doTriggers(@Nonnull Entity affectedEntity, int count) {
        for (TickCounterTrigger trigger : triggerList) {
            try {
                if (trigger.triggerAndReturnNeedClean(affectedEntity, count)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void addTrigger(TickCounterTrigger trigger) {
        synchronized (this) {
            triggerList.add(trigger);
        }
    }

    public void removeTrigger(TickCounterTrigger trigger) {
        synchronized (this) {
            triggerList.remove(trigger);
        }
    }
}
