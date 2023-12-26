package org.ricetea.barleyteaapi.api.task;

import org.ricetea.barleyteaapi.internal.task.TaskServiceImpl;

import javax.annotation.Nonnull;
import java.util.concurrent.Future;

public interface TaskService {
    @Nonnull
    static TaskService getInstance() {
        return TaskServiceImpl.getInstance();
    }

    @Nonnull
    Future<?> runTask(@Nonnull Runnable runnable);

    @Nonnull
    Future<?> runTaskLater(@Nonnull Runnable runnable, long delay);

    @Nonnull
    Future<?> runTaskTimer(@Nonnull Runnable runnable, long initialDelay, long interval);

    void shutdown();
}
