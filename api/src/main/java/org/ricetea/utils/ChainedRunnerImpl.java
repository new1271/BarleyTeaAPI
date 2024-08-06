package org.ricetea.utils;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.Queue;

final class ChainedRunnerImpl implements ChainedRunner {

    private final Lazy<Queue<Runnable>> runnableQueueLazy = Lazy.create(ArrayDeque::new);

    private int count;

    private boolean safeMode;

    ChainedRunnerImpl() {
        safeMode = false;
        count = 0;
    }

    @Override
    @Nonnull
    public ChainedRunner attach(@Nonnull Runnable runnable) {
        runnableQueueLazy.get().offer(runnable);
        count++;
        return this;
    }

    @Override
    @Nonnull
    public ChainedRunner safeMode(boolean safeMode) {
        this.safeMode = safeMode;
        return this;
    }

    @Override
    @Nonnull
    public ChainedRunner freeze() {
        int count = this.count;
        if (count <= 0)
            return ChainedRunner.empty();
        Queue<Runnable> runnableQueue = runnableQueueLazy.getUnsafe();
        if (runnableQueue == null)
            return ChainedRunner.empty();
        if (count == 1) {
            Runnable runnable = runnableQueue.poll();
            if (runnable == null)
                return ChainedRunner.empty();
            return new ChainedRunnerFreezedSingleImpl(runnable, safeMode);
        }
        return new ChainedRunnerFreezedImpl(runnableQueue.toArray(Runnable[]::new), safeMode);
    }

    @Override
    public boolean isFreezed() {
        return false;
    }

    @Override
    public boolean isInSafeMode() {
        return safeMode;
    }

    @Override
    public boolean isEmpty() {
        return count <= 0;
    }

    @Override
    public void run() {
        Queue<Runnable> runnableQueue = runnableQueueLazy.getUnsafe();
        if (runnableQueue == null)
            return;
        boolean safeMode = this.safeMode;
        Runnable runnable;
        while ((runnable = runnableQueue.poll()) != null) {
            if (safeMode)
                ObjectUtil.tryCall(runnable, Runnable::run);
            else
                runnable.run();
        }
    }
}
