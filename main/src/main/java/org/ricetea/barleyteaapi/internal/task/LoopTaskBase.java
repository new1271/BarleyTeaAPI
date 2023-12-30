package org.ricetea.barleyteaapi.internal.task;

import com.google.common.base.Stopwatch;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.task.LoopTask;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;

@ApiStatus.Internal
public abstract class LoopTaskBase implements LoopTask {

    private final long stepTime;
    @Nonnull
    private final Lazy<Stopwatch> stopwatchLazy = Lazy.create(Stopwatch::createUnstarted);
    private boolean started;

    public LoopTaskBase(long stepTime) {
        this.stepTime = stepTime;
    }

    @Override
    public long getStepTime() {
        return stepTime;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public void setStarted(boolean started) {
        this.started = started;
    }

    @Override
    public @Nonnull Stopwatch getStopwatch() {
        return stopwatchLazy.get();
    }
}
