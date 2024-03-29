package org.ricetea.barleyteaapi.internal.task;

import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.task.LoopTask;

@ApiStatus.Internal
public abstract class LoopTaskBase implements LoopTask {
    private final long stepTime;
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
}
