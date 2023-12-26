package org.ricetea.barleyteaapi.internal.entity.counter;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.api.entity.counter.TickCounterTrigger;

import javax.annotation.Nonnull;
import java.util.function.IntUnaryOperator;

public class PersistentTickCounterImpl extends TickCounterBase {

    public PersistentTickCounterImpl(@Nonnull NamespacedKey key, @Nonnull IntUnaryOperator operator,
                                     @Nonnull TickCounterTrigger trigger, int startValue) {
        super(key, operator, trigger, startValue);
    }

    @Override
    public int getCounter(@Nonnull Entity entity) {
        return entity.getPersistentDataContainer().getOrDefault(getKey(), PersistentDataType.INTEGER,
                getStartValue());
    }

    @Override
    public void setCounter(@Nonnull Entity entity, int count) {
        entity.getPersistentDataContainer().set(getKey(), PersistentDataType.INTEGER, count);
    }

    @Override
    public void resetCounter(@Nonnull Entity entity) {
        entity.getPersistentDataContainer().set(getKey(), PersistentDataType.INTEGER, getStartValue());
    }

    @Override
    public void removeCounter(@Nonnull Entity entity) {
        entity.getPersistentDataContainer().remove(getKey());
    }
}
