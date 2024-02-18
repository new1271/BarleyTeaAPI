package org.ricetea.barleyteaapi.api.misc;

import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface RandomProvider {

    @Nonnull
    static RandomProvider getInstance() {
        return Objects.requireNonNull(getInstanceUnsafe());
    }

    @Nullable
    static RandomProvider getInstanceUnsafe() {
        return Bukkit.getServicesManager().load(RandomProvider.class);
    }

    default boolean nextBoolean() {
        return nextByte((byte) 0b10) == 0b01;
    }

    default byte nextByte() {
        return nextByte(Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    default byte nextByte(byte bound) {
        return nextByte((byte) 0, bound);
    }

    default byte nextByte(byte start, byte bound) {
        return (byte) nextShort(start, bound);
    }

    default short nextShort() {
        return nextShort(Short.MIN_VALUE, Short.MAX_VALUE);
    }

    default short nextShort(short bound) {
        return nextShort((short) 0, bound);
    }

    default short nextShort(short start, short bound) {
        return (short) nextInt(start, bound);
    }

    default int nextInt() {
        return nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    default int nextInt(int bound) {
        return nextInt(0, bound);
    }

    int nextInt(int start, int bound);

    default long nextLong() {
        return nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    default long nextLong(long bound) {
        return nextLong(0L, bound);
    }

    default long nextLong(long start, long bound) {
        return ((long) nextInt((int) (start >>> 32), (int) (bound >>> 32))) << 32 |
                (long) nextInt((int) (start & 0x0000FFFF), (int) (bound & 0x0000FFFF));
    }

    default float nextFloat() {
        return nextFloat(0, 1);
    }

    default float nextFloat(float bound) {
        return nextFloat(0.0f, bound);
    }

    default float nextFloat(float start, float bound) {
        return (float) nextDouble(start, bound);
    }

    default double nextDouble() {
        return nextDouble(0, 1);
    }

    default double nextDouble(double bound) {
        return nextDouble(0.0, bound);
    }

    double nextDouble(double start, double bound);
}
