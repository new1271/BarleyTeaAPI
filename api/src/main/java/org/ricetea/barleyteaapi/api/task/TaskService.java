package org.ricetea.barleyteaapi.api.task;

import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.Future;

public interface TaskService {
    @Nonnull
    static TaskService getInstance() {
        return Objects.requireNonNull(getInstanceUnsafe());
    }

    @Nullable
    static TaskService getInstanceUnsafe() {
        return Bukkit.getServicesManager().load(TaskService.class);
    }

    @Nonnull
    Future<?> runTask(@Nonnull Runnable runnable);

    @Nonnull
    Future<?> runTaskLater(@Nonnull Runnable runnable, long delay);

    @Nonnull
    Future<?> runTaskTimer(@Nonnull Runnable runnable, long initialDelay, long interval);

    void shutdown();
}