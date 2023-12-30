package org.ricetea.barleyteaapi.api.entity.counter;

import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.internal.entity.counter.AsyncTickingServiceImpl;
import org.ricetea.barleyteaapi.internal.entity.counter.SyncTickingServiceImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    void addCounter(@Nonnull Entity entity, @Nonnull TickCounter counter);

    void removeCounter(@Nonnull Entity entity, @Nonnull TickCounter counter);

    void clearCounter(@Nonnull Entity entity);

    void shutdown();
}
