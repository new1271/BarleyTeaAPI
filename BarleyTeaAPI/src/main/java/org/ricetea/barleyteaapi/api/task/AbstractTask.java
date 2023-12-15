package org.ricetea.barleyteaapi.api.task;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

public abstract class AbstractTask implements Runnable {

    protected boolean isRunning;
    int intervalMax, intervalMin;
    Stopwatch stopwatch;

    protected AbstractTask(int intervalMax, int intervalMin) {
        this.intervalMax = intervalMax > 50 ? intervalMax : 50;
        this.intervalMin = intervalMin > 0 ? intervalMin : 0;
        stopwatch = Stopwatch.createUnstarted();
    }

    public void start() {
        isRunning = true;
        TaskService.run(this, intervalMax);
    }

    public void stop() {
        isRunning = false;
    }

    @Override
    public void run() {
        stopwatch.start();
        try {
            runInternal();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopwatch.stop();
        if (isRunning) {
            long howlongGoes = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            if (howlongGoes < intervalMax) {
                TaskService.run(this, Math.max(intervalMax - howlongGoes, intervalMin));
            } else {
                TaskService.run(this, intervalMin);
            }
        }
    }

    protected abstract void runInternal();
}
