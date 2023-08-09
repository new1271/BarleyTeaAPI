package org.ricetea.barleyteaapi.api.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.util.Lazy;

public class TaskService {

    private static Lazy<TaskService> lazyInst = new Lazy<>(TaskService::new);

    ScheduledExecutorService executorService;

    private TaskService() {
        executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public static void run(Runnable task) {
        if (BarleyTeaAPI.checkPluginUsable())
            lazyInst.get().doRun(task);
        else
            shutdown();
    }

    public static void run(Runnable task, long delay) {
        if (BarleyTeaAPI.checkPluginUsable())
            lazyInst.get().doRun(task, delay);
        else
            shutdown();
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
