package org.ricetea.barleyteaapi.internal.misc;

import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.misc.RandomProvider;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

@ApiStatus.Internal
public final class ThreadLocalRandomProviderImpl implements RandomProvider {

    @Nonnull
    private ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }

    @Override
    public int nextInt() {
        return getRandom().nextInt();
    }

    @Override
    public int nextInt(int start, int bound) {
        return getRandom().nextInt(start, bound);
    }

    @Override
    public long nextLong() {
        return getRandom().nextLong();
    }

    @Override
    public long nextLong(long start, long bound) {
        return getRandom().nextLong(start, bound);
    }

    @Override
    public float nextFloat() {
        return getRandom().nextFloat();
    }

    @Override
    public float nextFloat(float start, float bound) {
        return getRandom().nextFloat(start, bound);
    }

    @Override
    public double nextDouble() {
        return getRandom().nextDouble();
    }

    @Override
    public double nextDouble(double start, double bound) {
        return getRandom().nextDouble(start, bound);
    }
}
