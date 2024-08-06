package org.ricetea.utils;

import javax.annotation.Nonnull;

class ObjectWrapperImpl<T> implements ObjectWrapper<T> {

    @Nonnull
    private final T obj;

    private boolean called;

    ObjectWrapperImpl(@Nonnull T obj) {
        this.obj = obj;
        called = false;
    }

    @Nonnull
    @Override
    public T get() {
        called = true;
        return obj;
    }

    @Override
    public boolean isCalled() {
        return !called;
    }
}
