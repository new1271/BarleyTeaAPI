package org.ricetea.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Property<T> extends UnaryOperator<T>, Supplier<T>, Consumer<T> {

    @Nullable
    T get();

    @Nullable
    T set(T obj);

    @Nonnull
    PropertyType getPropertyType();

    default T apply(T obj) {
        return set(obj);
    }

    default void accept(T obj) {
        set(obj);
    }

    @Nonnull
    public static <T> Property<T> create(@Nonnull Supplier<T> getMethod, @Nonnull Consumer<T> setMethod) {
        return new DefaultPropertyImpl<>(getMethod, setMethod);
    }

    @Nonnull
    public static <T> Property<T> readonly(@Nonnull Supplier<T> getMethod) {
        return new DefaultPropertyImpl<>(getMethod, null);
    }

    @Nonnull
    public static <T> Property<T> writeonly(@Nonnull Consumer<T> setMethod) {
        return new DefaultPropertyImpl<>(null, setMethod);
    }

    public enum PropertyType {
        ReadWrite,
        ReadOnly,
        WriteOnly
    }

    static class DefaultPropertyImpl<T> implements Property<T> {

        @Nullable
        private final Supplier<T> getMethod;

        @Nullable
        private final Consumer<T> setMethod;

        @Nonnull
        private final PropertyType type;

        public DefaultPropertyImpl(@Nullable Supplier<T> getMethod, @Nullable Consumer<T> setMethod) {
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
}
