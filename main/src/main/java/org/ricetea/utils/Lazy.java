package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;
import java.util.function.Supplier;

public class Lazy<T> implements Property<T> {

    @Nonnull
    protected final Supplier<T> supplier;

    @Nullable
    protected T realObj;

    protected Lazy(@Nonnull Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Nonnull
    public static <T> Lazy<T> create(@Nonnull Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }

    @Nonnull
    public static <T> Lazy<T> createInThreadSafe(@Nonnull Supplier<T> supplier) {
        return new ThreadSafeImpl<>(supplier);
    }

    @Nonnull
    public T get() {
        T obj = realObj;
        if (obj == null) {
            return realObj = Objects.requireNonNull(supplier.get());
        }
        return obj;
    }

    @Nullable
    public T getUnsafe() {
        return realObj;
    }

    @Override
    @Nullable
    public T set(T obj) {
        throw new UnsupportedOperationException("this property is read-only!");
    }

    @Override
    @Nonnull
    public PropertyType getPropertyType() {
        return PropertyType.ReadOnly;
    }

    @ThreadSafe
    private static final class ThreadSafeImpl<T> extends Lazy<T> {

        @Nonnull
        private final Object syncRoot = new Object();

        private ThreadSafeImpl(@Nonnull Supplier<T> supplier) {
            super(supplier);
        }

        @Override
        @Nonnull
        public T get() {
            T obj = realObj;
            if (obj == null) {
                synchronized (syncRoot) {
                    UnsafeHelper.getUnsafe().fullFence();
                    obj = realObj;
                    if (obj == null) {
                        obj = realObj = Objects.requireNonNull(supplier.get());
                    }
                }
            }
            return obj;
        }
    }
}
