package org.ricetea.utils;

import javax.annotation.Nonnull;

class ChainedRunnerFreezedSingleImpl implements ChainedRunner {

    @Nonnull
    private final Runnable runnable;

    private final boolean safeMode;

    ChainedRunnerFreezedSingleImpl(@Nonnull Runnable runnable, boolean safeMode) {
        this.runnable = runnable;
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
        if (safeMode) {
            ObjectUtil.tryCall(runnable, Runnable::run);
        } else {
            runnable.run();
        }
    }
}
