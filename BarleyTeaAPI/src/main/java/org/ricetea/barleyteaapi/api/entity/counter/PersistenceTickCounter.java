package org.ricetea.barleyteaapi.api.entity.counter;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PersistenceTickCounter extends AbstractTickCounter {

    final int startValue;

    public PersistenceTickCounter(@Nonnull NamespacedKey identifierKey,
            @Nullable TickingOperationFunction function) {
        this(identifierKey, function, 0, (TickCounterTrigger[]) null);
    }

    @SafeVarargs
    public PersistenceTickCounter(@Nonnull NamespacedKey identifierKey,
            @Nullable TickingOperationFunction function, @Nullable TickCounterTrigger... predicates) {
        this(identifierKey, function, 0, predicates);
    }

    public PersistenceTickCounter(@Nonnull NamespacedKey identifierKey,
            @Nullable TickingOperationFunction function, int startValue) {
        this(identifierKey, function, startValue, (TickCounterTrigger[]) null);
    }

    @SafeVarargs
    public PersistenceTickCounter(@Nonnull NamespacedKey identifierKey,
            @Nullable TickingOperationFunction function, int startValue, @Nullable TickCounterTrigger... predicates) {
        super(identifierKey, function, predicates);
        this.startValue = startValue;
    }

    @Override
    protected int getCounter(@Nonnull Entity affectedEntity) {
        return affectedEntity.getPersistentDataContainer().getOrDefault(getKey(), PersistentDataType.INTEGER,
                startValue);
    }

    @Override
    protected void setCounter(@Nonnull Entity affectedEntity, int count) {
        affectedEntity.getPersistentDataContainer().set(getKey(), PersistentDataType.INTEGER, count);
    }

    @Override
    protected void resetCounter(@Nonnull Entity affectedEntity) {
        affectedEntity.getPersistentDataContainer().set(getKey(), PersistentDataType.INTEGER, startValue);
    }

    @Override
    public void cleanCounter(@Nonnull Entity affectedEntity) {
        affectedEntity.getPersistentDataContainer().remove(getKey());
    }
}
