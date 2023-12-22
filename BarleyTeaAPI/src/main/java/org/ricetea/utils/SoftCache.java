package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.SoftReference;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class SoftCache<T> extends Cache<T> {

    @Nullable
    protected SoftReference<T> realObj;

    public static <T> SoftCache<T> create(@Nonnull Supplier<T> supplier) {
        return new Impl<>(supplier);
    }

    public static <T> SoftCache<T> createInThreadSafe(@Nonnull Supplier<T> supplier) {
        return new ThreadSafeImpl<>(supplier);
    }

    @Override
    @Nonnull
    public T get() {
        T obj = ObjectUtil.safeMap(realObj, SoftReference::get);
        if (obj == null) {
            return Objects.requireNonNull((realObj = new SoftReference<>(get0())).get());
        }
        return obj;
    }

    private static class Impl<T> extends SoftCache<T> {

        @Nonnull
        protected final Supplier<T> supplier;

        protected Impl(@Nonnull Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Nonnull
        @Override
        protected T get0() {
            return supplier.get();
        }
    }

    private static final class ThreadSafeImpl<T> extends Impl<T> {

        @Nonnull
        private final Object syncRoot = new Object();

        private ThreadSafeImpl(@Nonnull Supplier<T> supplier) {
            super(supplier);
        }

        @Override
        @Nonnull
        public T get() {
            T obj = ObjectUtil.safeMap(realObj, SoftReference::get);
            if (obj == null) {
                synchronized (syncRoot) {
                    obj = super.get();
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
