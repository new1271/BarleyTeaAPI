package org.ricetea.barleyteaapi.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SimpleProperty<T> implements Property<T> {

    @Nullable
    private final Supplier<T> getMethod;

    @Nullable
    private final Consumer<T> setMethod;

    @Nonnull
    private final PropertyType type;

    public SimpleProperty(@Nullable Supplier<T> getMethod, @Nullable Consumer<T> setMethod) {
        this.getMethod = getMethod;
        this.setMethod = setMethod;
        if (getMethod == null) {
            if (setMethod == null) {
                throw new UnsupportedOperationException("'getMethod' and 'setMethod' cannot be both null");
            } else {
                type = PropertyType.WriteOnly;
            }
        } else {
            if (setMethod == null) {
                type = PropertyType.ReadOnly;
            } else {
                type = PropertyType.ReadWrite;
            }
        }
    }

    @Override
    @Nullable
    public T get() {
        Supplier<T> getMethod = this.getMethod;
        if (getMethod == null) {
            throw new UnsupportedOperationException("this property is write-only!");
        } else {
            return getMethod.get();
        }
    }

    @Override
    @Nullable
    public T set(T obj) {
        Consumer<T> setMethod = this.setMethod;
        if (setMethod == null) {
            throw new UnsupportedOperationException("this property is read-only!");
        } else {
            setMethod.accept(obj);
            Supplier<T> getMethod = this.getMethod;
            if (getMethod == null)
                return obj;
            else
                return getMethod.get();
        }
    }

    @Override
    @Nonnull
    public PropertyType getPropertyType() {
        return type;
    }
}
