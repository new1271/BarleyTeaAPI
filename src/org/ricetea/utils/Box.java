package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Box<T> implements Property<T> {
    protected T obj;

    protected Box(@Nullable T obj) {
        this.obj = obj;
    }

    @Nonnull
    public static <T> Box<T> box(@Nullable T obj) {
        return new Box<T>(obj);
    }

    @Nonnull
    public static <T> Box<T> boxInThreadSafe(@Nullable T obj) {
        return new ThreadSafeImpl<T>(obj);
    }

    @Nullable
    public static <T> T unbox(@Nullable Box<T> box) {
        return box == null ? null : box.unbox();
    }

    @Nullable
    public T unbox() {
        return exchange(null);
    }

    @Nullable
    public T exchange(@Nullable T obj) {
        T oldObj = this.obj;
        this.obj = obj;
        return oldObj;
    }

    @Nullable
    public T get() {
        return obj;
    }

    @Override
    public T set(T obj) {
        return this.obj = obj;
    }

    public boolean contains(@Nullable T obj) {
        return this.obj == obj;
    }

    public boolean isNull() {
        return this.obj == null;
    }

    public boolean isNotNull() {
        return this.obj != null;
    }

    public static <T> boolean isNull(@Nullable Box<T> box) {
        return box == null || box.isNull();
    }

    public static <T> boolean isNotNull(@Nullable Box<T> box) {
        return box != null && box.isNotNull();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) || this.obj == obj || (this.obj == null ? obj.equals(this.obj) : this.obj.equals(obj));
    }

    @Override
    @Nonnull
    public PropertyType getPropertyType() {
        return PropertyType.ReadWrite;
    }

    private static final class ThreadSafeImpl<T> extends Box<T> {

        @Nonnull
        private final Object syncRoot = new Object();

        protected ThreadSafeImpl(@Nullable T obj) {
            super(obj);
        }

        @Override
        @Nullable
        public T get() {
            synchronized (syncRoot) {
                return obj;
            }
        }

        @Override
        @Nullable
        public T set(@Nullable T value) {
            synchronized (syncRoot) {
                return obj = value;
            }
        }

        @Override
        @Nullable
        public T exchange(@Nullable T obj) {
            synchronized (syncRoot) {
                T oldObj = this.obj;
                this.obj = obj;
                return oldObj;
            }
        }
    }
}
