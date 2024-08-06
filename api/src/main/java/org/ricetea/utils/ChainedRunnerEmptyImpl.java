package org.ricetea.utils;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

class ChainedRunnerEmptyImpl implements ChainedRunner {

    private static final Lazy<ChainedRunnerEmptyImpl> _inst = Lazy.create(ChainedRunnerEmptyImpl::new);

    private ChainedRunnerEmptyImpl() {
    }

    @Nonnull
    public static ChainedRunnerEmptyImpl getInstance() {
        return _inst.get();
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
        return true;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public void run() {
        //Do nothing
    }

    @Override
    public void run(@Nonnull Plugin plugin, @Nonnull BukkitScheduler scheduler) {
        //Do nothing
    }

    @Override
    public void run(@Nonnull Consumer<Runnable> consumer) {
        //Do nothing
    }
}
