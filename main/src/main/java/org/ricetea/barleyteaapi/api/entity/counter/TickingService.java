package org.ricetea.barleyteaapi.api.entity.counter;

import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.internal.entity.counter.AsyncTickingServiceImpl;
import org.ricetea.barleyteaapi.internal.entity.counter.SyncTickingServiceImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public interface TickingService {

    @Nonnull
    static TickingService syncService() {
        return SyncTickingServiceImpl.getInstance();
    }

    @Nullable
    static TickingService syncServiceUnsafe() {
        return SyncTickingServiceImpl.getInstanceUnsafe();
    }

    @Nonnull
    static TickingService asyncService() {
        return AsyncTickingServiceImpl.getInstance();
    }

    @Nullable
    static TickingService asyncServiceUnsafe() {
        return AsyncTickingServiceImpl.getInstanceUnsafe();
    }

    default void addCounter(@Nonnull Entity entity, @Nonnull TickCounter counter) {
        addCounter(entity.getUniqueId(), counter);
    }

    void addCounter(@Nonnull UUID uuid, @Nonnull TickCounter counter);

    default void removeCounter(@Nonnull Entity entity, @Nonnull TickCounter counter) {
        removeCounter(entity.getUniqueId(), counter);
    }

    void removeCounter(@Nonnull UUID uuid, @Nonnull TickCounter counter);

    default void clearCounter(@Nonnull Entity entity) {
        clearCounter(entity.getUniqueId());
    }

    void clearCounter(@Nonnull UUID uuid);

    void shutdown();
}
