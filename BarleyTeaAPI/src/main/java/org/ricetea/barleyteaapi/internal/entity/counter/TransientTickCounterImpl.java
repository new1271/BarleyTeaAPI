package org.ricetea.barleyteaapi.internal.entity.counter;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.api.entity.counter.TickCounterTrigger;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntUnaryOperator;

public class TransientTickCounterImpl extends TickCounterBase {
    private final ConcurrentHashMap<UUID, Integer> storer = new ConcurrentHashMap<>();

    public TransientTickCounterImpl(@Nonnull NamespacedKey key, @Nonnull IntUnaryOperator operator,
                                    @Nonnull TickCounterTrigger trigger, int startValue) {
        super(key, operator, trigger, startValue);
    }

    @Override
    public int getCounter(@Nonnull Entity entity) {
        return storer.getOrDefault(entity.getUniqueId(), getStartValue());
    }

    @Override
    public void setCounter(@Nonnull Entity entity, int count) {
        storer.put(entity.getUniqueId(), count);
    }

    @Override
    public void resetCounter(@Nonnull Entity entity) {
        storer.put(entity.getUniqueId(), getStartValue());
    }

    @Override
    public void removeCounter(@Nonnull Entity entity) {
        storer.remove(entity.getUniqueId());
    }
}
