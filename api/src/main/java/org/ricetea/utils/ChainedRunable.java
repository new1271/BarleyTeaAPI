package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public final class ChainedRunable implements Runnable {

    @Nonnull
    private final List<Runnable> runableList;

    public ChainedRunable() {
        runableList = new LinkedList<>();
    }

    public ChainedRunable(@Nonnull Runnable... runnables) {
        this();
        for (Runnable runnable : runnables) {
            attach(runnable);
        }
    }

    public ChainedRunable(@Nonnull Collection<Runnable> runnables) {
        this();
        runnables.forEach(this::attach);
    }

    public void attach(@Nullable Runnable runnable) {
        if (runnable == null)
            return;
        runableList.add(runnable);
    }

    public void detach(@Nullable Runnable runnable) {
        if (runnable == null)
            return;
        runableList.remove(runnable);
    }

    public boolean isEmpty() {
        return runableList.isEmpty();
    }

    @Override
    public void run() {
        runableList.forEach(Runnable::run);
    }
}
