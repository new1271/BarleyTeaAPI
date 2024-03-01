package org.ricetea.barleyteaapi.internal.task;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.task.TaskService;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

@Singleton
@ApiStatus.Internal
public final class TaskServiceImpl implements TaskService {

    @Nonnull
    private final ForkJoinPool pool = ForkJoinPool.commonPool();

    @Nonnull
    @Override
    public Future<?> runTask(@Nonnull Runnable runnable) {
        return pool.submit(runnable);
    }

    @Nonnull
    @Override
    public Future<?> runTaskLater(@Nonnull Runnable runnable, @Nonnegative long delay) {
        if (delay == 0)
            return runTask(runnable);
        return pool.submit(new DelayTask(pool, runnable, delay));
    }

    @Nonnull
    @Override
    public Future<?> runTaskTimer(@Nonnull Runnable runnable, @Nonnegative long initialDelay, @Nonnegative long interval) {
        ScheduledTask task = new ScheduledTask(pool, runnable, interval);
        if (initialDelay == 0)
            return pool.submit(task);
        return runTaskLater(task, initialDelay);
    }

    private static final class DelayTask implements Runnable {

        private final ForkJoinPool pool;
        private final Runnable laterRunning;
        private final long delay;

        public DelayTask(@Nonnull final ForkJoinPool pool, @Nonnull final Runnable laterRunning, final long delay) {
            this.pool = pool;
            this.laterRunning = laterRunning;
            this.delay = delay;
        }

        @Override
        public void run() {
            if (Bukkit.isPrimaryThread()) {
                pool.submit(this);
            } else {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {
                }
                pool.submit(laterRunning);
            }
        }
    }

    private static final class ScheduledTask implements Runnable {

        private final DelayTask delayTask;
        private final ForkJoinPool pool;
        private final Runnable laterRunning;

        public ScheduledTask(@Nonnull final ForkJoinPool pool, @Nonnull final Runnable laterRunning, final long interval) {
            this.pool = pool;
            this.laterRunning = laterRunning;
            this.delayTask = new DelayTask(pool, this, interval);
        }

        @Override
        public void run() {
            if (Bukkit.isPrimaryThread()) {
                pool.submit(this);
            } else {
                pool.submit(laterRunning);
                pool.submit(delayTask);
            }
        }
    }
}
