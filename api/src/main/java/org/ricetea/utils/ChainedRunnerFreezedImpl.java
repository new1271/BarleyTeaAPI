package org.ricetea.utils;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

class ChainedRunnerFreezedImpl implements ChainedRunner {

    @Nonnull
    private final Runnable[] runnables;

    private final boolean safeMode;

    ChainedRunnerFreezedImpl(@Nonnull Runnable[] runnables, boolean safeMode) {
        this.runnables = runnables;
        this.safeMode = safeMode;
    }

    @Nonnull
    @Override
    public ChainedRunner attach(@Nonnull Runnable runnable) {
        return this;
    }

    @Nonnull
    @Override
    public ChainedRunner safeMode(boolean safeMode) {
        return this;
    }

    @Nonnull
    @Override
    public ChainedRunner freeze() {
        return this;
    }

    @Override
    public boolean isFreezed() {
        return true;
    }

    @Override
    public boolean isInSafeMode() {
        return safeMode;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void run() {
        boolean safeMode = this.safeMode;
        if (safeMode) {
            Consumer<Runnable> consumer = Runnable::run;
            for (Runnable runnable : runnables) {
                ObjectUtil.tryCall(runnable, consumer);
            }
        } else {
            for (Runnable runnable : runnables) {
                if (runnable == null)
                    continue;
                runnable.run();
            }
        }
    }
}
