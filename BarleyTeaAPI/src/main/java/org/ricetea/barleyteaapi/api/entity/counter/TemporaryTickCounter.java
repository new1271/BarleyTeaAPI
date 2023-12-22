package org.ricetea.barleyteaapi.api.entity.counter;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

public class TemporaryTickCounter extends AbstractTickCounter {

    final int startValue;
    private final HashMap<UUID, Integer> storer;

    public TemporaryTickCounter(@Nonnull NamespacedKey identifierKey,
                                @Nullable TickingOperationFunction function) {
        this(identifierKey, function, 0, (TickCounterTrigger[]) null);
    }

    public TemporaryTickCounter(@Nonnull NamespacedKey identifierKey,
                                @Nullable TickingOperationFunction function, @Nullable TickCounterTrigger... predicates) {
        this(identifierKey, function, 0, predicates);
    }

    public TemporaryTickCounter(@Nonnull NamespacedKey identifierKey,
                                @Nullable TickingOperationFunction function, int startValue) {
        this(identifierKey, function, startValue, (TickCounterTrigger[]) null);
    }

    public TemporaryTickCounter(@Nonnull NamespacedKey identifierKey,
                                @Nullable TickingOperationFunction function, int startValue, @Nullable TickCounterTrigger... predicates) {
        super(identifierKey, function, predicates);
        this.startValue = startValue;
        storer = new HashMap<>();
    }

    @Override
    protected int getCounter(@Nonnull Entity affectedEntity) {
        return storer.getOrDefault(affectedEntity.getUniqueId(), startValue);
    }

    @Override
    protected void setCounter(@Nonnull Entity affectedEntity, int count) {
        storer.put(affectedEntity.getUniqueId(), count);
    }

    @Override
    protected void resetCounter(@Nonnull Entity affectedEntity) {
        storer.put(affectedEntity.getUniqueId(), startValue);
    }

    @Override
    public void cleanCounter(@Nonnull Entity affectedEntity) {
        storer.remove(affectedEntity.getUniqueId());
    }
}
