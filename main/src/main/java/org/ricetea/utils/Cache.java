package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class Cache<T> implements Property<T> {

    @Nullable
    protected T realObj;

    public static <T> Cache<T> create(@Nonnull Supplier<T> supplier) {
        return new Impl<>(supplier);
    }

    public static <T> Cache<T> createInThreadSafe(@Nonnull Supplier<T> supplier) {
        return new ThreadSafeImpl<>(supplier);
    }

    @Nonnull
    public T get() {
        return Objects.requireNonNull(getUnsafe());
    }

    @Nullable
    public T getUnsafe() {
        T obj = realObj;
        if (obj == null) {
            return realObj = get0();
        }
        return obj;
    }

    @Nullable
    protected abstract T get0();

    public void reset() {
        realObj = null;
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

    private static class Impl<T> extends Cache<T> {

        @Nonnull
        protected final Supplier<T> supplier;

        protected Impl(@Nonnull Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Nullable
        @Override
        protected T get0() {
            return supplier.get();
        }
    }

    @ThreadSafe
    private static final class ThreadSafeImpl<T> extends Impl<T> {

        @Nonnull
        private final Object syncRoot = new Object();

        private ThreadSafeImpl(@Nonnull Supplier<T> supplier) {
            super(supplier);
        }

        @Override
        @Nullable
        public T getUnsafe() {
            T obj = realObj;
            if (obj == null) {
                synchronized (syncRoot) {
                    UnsafeHelper.getUnsafe().fullFence();
                    obj = realObj;
                    if (obj == null) {
                        obj = realObj = super.getUnsafe();
                    }
                }
            }
            return obj;
        }

        @Override
        public void reset() {
            synchronized (syncRoot) {
                super.reset();
            }
        }
    }
}
