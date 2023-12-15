package org.ricetea.utils;

import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Cache<T> implements Property<T> {

    @Nonnull
    protected final Supplier<T> supplier;

    @Nullable
    protected T realObj;

    protected Cache(@Nonnull Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> Cache<T> create(@Nonnull Supplier<T> supplier) {
        return new Cache<>(supplier);
    }

    public static <T> Cache<T> createInThreadSafe(@Nonnull Supplier<T> supplier) {
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

    private static final class ThreadSafeImpl<T> extends Cache<T> {

        @Nonnull
        private final Object syncRoot = new Object();

        protected ThreadSafeImpl(@Nonnull Supplier<T> supplier) {
            super(supplier);
        }

        @Override
        @Nonnull
        public T get() {
            T obj = realObj;
            if (obj == null) {
                synchronized (syncRoot) {
                    obj = realObj;
                    if (obj == null) {
                        obj = realObj = Objects.requireNonNull(supplier.get());
                    }
                }
            }
            return obj;
        }

        @Override
        public void reset() {
            synchronized (syncRoot) {
                realObj = null;
            }
        }
    }
}
