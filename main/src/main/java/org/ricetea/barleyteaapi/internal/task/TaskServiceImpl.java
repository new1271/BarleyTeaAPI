package org.ricetea.barleyteaapi.internal.task;

import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.task.TaskService;
import org.ricetea.utils.Lazy;
import sun.misc.Unsafe;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
@ApiStatus.Internal
public final class TaskServiceImpl implements TaskService {

    @Nonnull
    private static final Lazy<TaskServiceImpl> _inst = Lazy.create(TaskServiceImpl::new);

    @Nonnull
    private final Object syncRoot = new Object();

    private ScheduledExecutorService executorService;

    private TaskServiceImpl() {
    }

    @Nonnull
    public static TaskServiceImpl getInstance() {
        return _inst.get();
    }

    @Nonnull
    private ScheduledExecutorService getExecutorService() {
        ScheduledExecutorService executorService = this.executorService;
        if (executorService == null) {
            synchronized (syncRoot) {
                Unsafe.getUnsafe().fullFence();
                executorService = this.executorService;
                if (executorService == null)
                    executorService = this.executorService =
                            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
            }
        }
        return executorService;
    }

    @Nonnull
    @Override
    public Future<?> runTask(@Nonnull Runnable runnable) {
        return getExecutorService().submit(runnable);
    }

    @Nonnull
    @Override
    public Future<?> runTaskLater(@Nonnull Runnable runnable, long delay) {
        return getExecutorService().schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    @Nonnull
    @Override
    public Future<?> runTaskTimer(@Nonnull Runnable runnable, long initialDelay, long interval) {
        return getExecutorService().scheduleAtFixedRate(runnable, initialDelay, interval, TimeUnit.MILLISECONDS);
    }

    @Override
    public void shutdown() {

    }
}
