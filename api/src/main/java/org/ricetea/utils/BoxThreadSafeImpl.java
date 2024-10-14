package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

class BoxThreadSafeImpl<T> implements Box<T> {

    @Nonnull
    private final AtomicReference<T> objReference;

    BoxThreadSafeImpl(@Nullable T obj) {
        objReference = new AtomicReference<>(obj);
    }

    @Nullable
    @Override
    public T unbox() {
        return objReference.get();
    }

    @Nullable
    @Override
    public T exchange(@Nullable T obj) {
        return objReference.getAndSet(obj);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj || super.equals(obj))
            return true;
        if (obj instanceof Box<?> anotherBox) {
            return Objects.equals(unbox(), anotherBox.unbox());
        }
        return false;
    }
}
