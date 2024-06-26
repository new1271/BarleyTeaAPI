package org.ricetea.barleyteaapi.api.task;

import com.google.common.base.Stopwatch;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

public interface LoopTask extends Runnable {

    ThreadLocal<Stopwatch> TLStopwatches = ThreadLocal.withInitial(Stopwatch::createUnstarted);

    @Nonnegative
    long getStepTime();

    boolean isStarted();

    void setStarted(boolean started);

    @Nullable
    default TaskOption[] getOptions() {
        return null;
    }

    @Nonnull
    default Stopwatch getStopwatch() {
        return TLStopwatches.get().reset();
    }

    default void start() {
        if (!isStarted()) {
            TaskService service = TaskService.getInstanceUnsafe();
            if (service != null) {
                setStarted(true);
                service.runTask(this, getOptions());
            }
        }
    }

    default void stop() {
        setStarted(false);
    }

    void runLoop();

    default void run() {
        Stopwatch stopwatch = getStopwatch();
        stopwatch.start();
        ObjectUtil.tryCall(this::runLoop);
        stopwatch.stop();
        if (isStarted()) {
            TaskService service = TaskService.getInstanceUnsafe();
            if (service == null) {
                stop();
                return;
            }
            long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            long stepTime = getStepTime();
            if (elapsed < stepTime) {
                service.runTaskLater(this, stepTime - elapsed, getOptions());
            } else {
                service.runTask(this, getOptions());
            }
        }
    }
}
