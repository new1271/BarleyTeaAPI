package org.ricetea.barleyteaapi.api.task;

import org.bukkit.Bukkit;

import javax.annotation.Nonnegative;
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
    Future<?> runTask(@Nonnull Runnable runnable, @Nullable TaskOption... options);

    @Nonnull
    Future<?> runTaskLater(@Nonnull Runnable runnable, @Nonnegative long delay, @Nullable TaskOption... options);

    @Nonnull
    Future<?> runTaskTimer(@Nonnull Runnable runnable, @Nonnegative long initialDelay,
                           @Nonnegative long interval, @Nullable TaskOption... options);

    void shutdown();
}
