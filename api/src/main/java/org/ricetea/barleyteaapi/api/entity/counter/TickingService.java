package org.ricetea.barleyteaapi.api.entity.counter;

import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.api.internal.entity.counter.TickingServices;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface TickingService {

    @Nonnull
    static TickingService syncService() {
        return TickingServices.getInstance().syncService();
    }

    @Nullable
    static TickingService syncServiceUnsafe() {
        return ObjectUtil.safeMap(TickingServices.getInstanceUnsafe(), TickingServices::syncService);
    }

    @Nonnull
    static TickingService asyncService() {
        return TickingServices.getInstance().asyncService();
    }

    @Nullable
    static TickingService asyncServiceUnsafe() {
        return ObjectUtil.safeMap(TickingServices.getInstanceUnsafe(), TickingServices::asyncService);
    }

    void addCounter(@Nonnull Entity entity, @Nonnull TickCounter counter);

    void removeCounter(@Nonnull Entity entity, @Nonnull TickCounter counter);

    void clearCounter(@Nonnull Entity entity);

    void shutdown();
}
