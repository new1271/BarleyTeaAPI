package org.ricetea.barleyteaapi.api.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.ricetea.barleyteaapi.util.Lazy;

public class TaskService {

    private static Lazy<TaskService> lazyInst = new Lazy<>(TaskService::new);

    ScheduledExecutorService executorService;

    private TaskService() {
        executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public static void run(Runnable task) {
        lazyInst.get().doRun(task);
    }

    public static void run(Runnable task, long delay) {
        lazyInst.get().doRun(task, delay);
    }

    public static void shutdown() {
        TaskService inst = lazyInst.getUnsafe();
        if (inst != null) {
            inst.doShutdown();
        }
    }

    void doRun(Runnable task) {
        if (!executorService.isShutdown() && !executorService.isTerminated()) {
            executorService.execute(task);
        }
    }

    void doRun(Runnable task, long delay) {
        if (!executorService.isShutdown() && !executorService.isTerminated()) {
            executorService.schedule(task, delay, TimeUnit.MILLISECONDS);
        }
    }

    void doShutdown() {
        if (!executorService.isShutdown() && !executorService.isTerminated()) {
            executorService.shutdownNow();
        }
    }
}
