package org.ricetea.utils;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public interface ChainedRunner extends Runnable {

    @Nonnull
    static ChainedRunner empty() {
        return ChainedRunnerEmptyImpl.getInstance();
    }

    @Nonnull
    static ChainedRunner create() {
        return new ChainedRunnerImpl();
    }

    @Nonnull
    static ChainedRunner createThreadSafe() {
        return new ChainedRunnerThreadSafeImpl();
    }

    @Nonnull
    ChainedRunner attach(@Nonnull Runnable runnable);

    @Nonnull
    ChainedRunner safeMode(boolean safeMode);

    @Nonnull
    ChainedRunner freeze();

    boolean isFreezed();

    boolean isInSafeMode();

    boolean isEmpty();

    void run();

    default void run(@Nonnull Plugin plugin, @Nonnull BukkitScheduler scheduler) {
        scheduler.runTask(plugin, this);
    }

    default void run(@Nonnull Consumer<Runnable> consumer) {
        consumer.accept(this);
    }
}
