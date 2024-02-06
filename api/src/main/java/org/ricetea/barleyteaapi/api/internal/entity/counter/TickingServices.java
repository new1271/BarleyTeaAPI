package org.ricetea.barleyteaapi.api.internal.entity.counter;

import org.bukkit.Bukkit;
import org.ricetea.barleyteaapi.api.entity.counter.TickingService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface TickingServices {

    @Nonnull
    static TickingServices getInstance() {
        return Objects.requireNonNull(getInstanceUnsafe());
    }

    @Nullable
    static TickingServices getInstanceUnsafe() {
        return Bukkit.getServicesManager().load(TickingServices.class);
    }

    @Nonnull
    TickingService syncService();

    @Nonnull
    TickingService asyncService();
}
