package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

class BoxThreadSafeImpl<T> implements Box<T> {

    @Nonnull
    private final Object syncRoot = new Object();

    @Nullable
    private T obj;

    BoxThreadSafeImpl(@Nullable T obj) {
        this.obj = obj;
    }

    @Nullable
    @Override
    public T unbox() {
        T obj;
        synchronized (syncRoot) {
            obj = this.obj;
        }
        return obj;
    }

    @Nullable
    @Override
    public T exchange(@Nullable T obj) {
        synchronized (syncRoot) {
            this.obj = obj;
        }
        return obj;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj || super.equals(obj))
            return true;
        if (obj instanceof Box<?> anotherBox) {
            synchronized (syncRoot) {
                return Objects.equals(obj, anotherBox.unbox());
            }
        }
        return false;
    }
}
