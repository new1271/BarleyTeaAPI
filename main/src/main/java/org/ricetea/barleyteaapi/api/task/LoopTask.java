package org.ricetea.barleyteaapi.api.task;

import com.google.common.base.Stopwatch;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public interface LoopTask extends Runnable {

    static ThreadLocal<Stopwatch> TLStopwatches = ThreadLocal.withInitial(Stopwatch::createUnstarted);

    @Nonnegative
    long getStepTime();

    boolean isStarted();

    void setStarted(boolean started);

    @Nonnull
    default Stopwatch getStopwatch() {
        return TLStopwatches.get().reset();
    }

    default void start() {
        if (!isStarted()) {
            setStarted(true);
            TaskService.getInstance().runTask(this);
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
            TaskService service = TaskService.getInstance();
            long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            long stepTime = getStepTime();
            if (elapsed < stepTime) {
                service.runTaskLater(this, stepTime - elapsed);
            } else {
                service.runTask(this);
            }
        }
    }
}
