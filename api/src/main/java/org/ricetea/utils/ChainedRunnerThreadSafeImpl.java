package org.ricetea.utils;

import javax.annotation.Nonnull;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

final class ChainedRunnerThreadSafeImpl implements ChainedRunner {

    private final Lazy<Queue<Runnable>> runnableQueueLazy = Lazy.createThreadSafe(ConcurrentLinkedDeque::new);

    private final AtomicInteger count;

    private final AtomicBoolean safeMode;

    ChainedRunnerThreadSafeImpl() {
        safeMode = new AtomicBoolean(false);
        count = new AtomicInteger(0);
    }

    @Override
    @Nonnull
    public ChainedRunner attach(@Nonnull Runnable runnable) {
        runnableQueueLazy.get().offer(runnable);
        count.incrementAndGet();
        return this;
    }

    @Override
    @Nonnull
    public ChainedRunner safeMode(boolean safeMode) {
        this.safeMode.set(safeMode);
        return this;
    }

    @Override
    @Nonnull
    public ChainedRunner freeze() {
        int count = this.count.get();
        if (count <= 0)
            return ChainedRunner.empty();
        Queue<Runnable> runnableQueue = runnableQueueLazy.getUnsafe();
        if (runnableQueue == null)
            return ChainedRunner.empty();
        if (count == 1) {
            Runnable runnable = runnableQueue.poll();
            if (runnable == null)
                return ChainedRunner.empty();
            return new ChainedRunnerFreezedSingleImpl(runnable, safeMode.get());
        }
        return new ChainedRunnerFreezedImpl(runnableQueue.toArray(Runnable[]::new), safeMode.get());
    }

    @Override
    public boolean isFreezed() {
        return false;
    }

    @Override
    public boolean isInSafeMode() {
        return safeMode.get();
    }

    @Override
    public boolean isEmpty() {
        return count.get() <= 0;
    }

    @Override
    public void run() {
        Queue<Runnable> runnableQueue = runnableQueueLazy.getUnsafe();
        if (runnableQueue == null)
            return;
        boolean safeMode = this.safeMode.get();
        Runnable runnable;
        while ((runnable = runnableQueue.poll()) != null) {
            if (safeMode)
                ObjectUtil.tryCall(runnable, Runnable::run);
            else
                runnable.run();
        }
    }
}
