package org.ricetea.utils;

import javax.annotation.Nullable;
import java.util.Objects;

class BoxImpl<T> implements Box<T> {

    @Nullable
    private T obj;

    BoxImpl(@Nullable T obj) {
        this.obj = obj;
    }

    @Nullable
    @Override
    public T unbox() {
        return obj;
    }

    @Nullable
    @Override
    public T exchange(@Nullable T obj) {
        T oldObj = this.obj;
        this.obj = obj;
        return oldObj;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj || super.equals(obj))
            return true;
        if (obj instanceof Box<?> anotherBox) {
            return Objects.equals(obj, anotherBox.unbox());
        }
        return false;
    }
}
