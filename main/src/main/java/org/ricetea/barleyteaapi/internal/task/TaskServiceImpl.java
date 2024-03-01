package org.ricetea.barleyteaapi.internal.task;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.task.TaskOption;
import org.ricetea.barleyteaapi.api.task.TaskService;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
@ApiStatus.Internal
public final class TaskServiceImpl implements TaskService {

    @Nonnull
    private final ForkJoinPool pool = ForkJoinPool.commonPool();

    @Nonnull
    private final Lazy<ScheduledExecutorService> executorServiceLazy = Lazy.createThreadSafe(() ->
            Executors.newScheduledThreadPool(Math.max(Runtime.getRuntime().availableProcessors() / 2, 2),
                    new ThreadFactory()));

    @Nonnull
    @Override
    public Future<?> runTask(@Nonnull Runnable runnable, @Nullable TaskOption... options) {
        boolean attachToParent = false, longRunning = false;
        if (options != null) {
            for (TaskOption option : options) {
                switch (option) {
                    case AttachToParent -> attachToParent = true;
                    case LongRunning -> longRunning = true;
                }
            }
        }
        return runTask(runnable, attachToParent, longRunning);
    }

    @Nonnull
    private Future<?> runTask(@Nonnull Runnable runnable, boolean attachToParent, boolean longRunning) {
        if (attachToParent) {
            return switch (getWorkerThreadState()) {
                case 1 -> {
                    if (longRunning) {
                        yield runTask(runnable, false, true);
                    } else {
                        FutureTask<Void> task = new FutureTask<>(runnable, null);
                        task.run();
                        yield task;
                    }
                }
                case 2 -> {
                    FutureTask<Void> task = new FutureTask<>(runnable, null);
                    task.run();
                    yield task;
                }
                default -> runTask(runnable, false, longRunning);
            };
        }
        if (longRunning) {
            return executorServiceLazy.get().submit(runnable);
        } else {
            return pool.submit(runnable);
        }
    }

    @Nonnull
    @Override
    public Future<?> runTaskLater(@Nonnull Runnable runnable, @Nonnegative long delay, @Nullable TaskOption... options) {
        boolean attachToParent = false, longRunning = false;
        if (options != null) {
            for (TaskOption option : options) {
                switch (option) {
                    case AttachToParent -> attachToParent = true;
                    case LongRunning -> longRunning = true;
                }
            }
        }
        if (delay == 0)
            return runTask(runnable, attachToParent, longRunning);
        else
            return runTaskLater(runnable, delay, longRunning);
    }

    @Nonnull
    private Future<?> runTaskLater(@Nonnull Runnable runnable, @Nonnegative long delay, boolean longRunning) {
        if (longRunning) {
            return executorServiceLazy.get().schedule(runnable, delay, TimeUnit.MILLISECONDS);
        } else {
            return pool.submit(new DelayTask(pool, runnable, delay));
        }
    }

    @Nonnull
    @Override
    public Future<?> runTaskTimer(@Nonnull Runnable runnable, @Nonnegative long initialDelay,
                                  @Nonnegative long interval, @Nullable TaskOption... options) {
        boolean attachToParent = false, longRunning = false;
        if (options != null) {
            for (TaskOption option : options) {
                switch (option) {
                    case AttachToParent -> attachToParent = true;
                    case LongRunning -> longRunning = true;
                }
            }
        }
        if (interval == 0) {
            if (initialDelay == 0)
                return runTask(runnable, attachToParent, longRunning);
            else
                return runTaskLater(runnable, initialDelay, longRunning);
        } else {
            return runTaskTimer(runnable, initialDelay, interval, longRunning);
        }
    }

    @Nonnull
    private Future<?> runTaskTimer(@Nonnull Runnable runnable,
                                   @Nonnegative long initialDelay,
                                   @Nonnegative long interval,
                                   boolean longRunning) {
        if (longRunning) {
            return executorServiceLazy.get()
                    .scheduleAtFixedRate(runnable, initialDelay, interval, TimeUnit.MILLISECONDS);
        } else {
            return pool.submit(
                    new DelayTask(
                            pool,
                            new ScheduledTask(pool, runnable, interval),
                            initialDelay
                    )
            );
        }
    }

    @Override
    public void shutdown() {
        ObjectUtil.safeCall(executorServiceLazy.getUnsafe(), ExecutorService::shutdown);
    }

    private int getWorkerThreadState() {
        Thread thread = Thread.currentThread();
        if (thread instanceof ForkJoinWorkerThread)
            return 1;
        else if (thread instanceof ThreadFactory.TaggedThread)
            return 2;
        return 0;
    }

    private static class ThreadFactory implements java.util.concurrent.ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        ThreadFactory() {
            group = Thread.currentThread().getThreadGroup();
            namePrefix = "BarleyTeaAPI-Task-Worker-";
        }

        @Nonnull
        public Thread newThread(@Nonnull Runnable r) {
            Thread t = new TaggedThread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }

        public static class TaggedThread extends Thread {
            public TaggedThread(@Nullable ThreadGroup group, @Nonnull Runnable target,
                                @Nonnull String name, @Nonnegative long stackSize) {
                super(group, target, name, stackSize);
            }
        }
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
