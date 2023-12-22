package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface BiProperty<T, R> extends BiConsumer<T, R>, BiFunction<T, R, R> {

    @Nonnull
    static <T, R> BiProperty<T, R> create(@Nonnull Function<T, R> getMethod,
                                          @Nonnull BiConsumer<T, R> setMethod) {
        return new DefaultPropertyImpl<>(getMethod, setMethod);
    }

    @Nonnull
    static <T, R> BiProperty<T, R> readonly(@Nonnull Function<T, R> getMethod) {
        return new DefaultPropertyImpl<>(getMethod, null);
    }

    @Nonnull
    static <T, R> BiProperty<T, R> writeonly(@Nonnull BiConsumer<T, R> setMethod) {
        return new DefaultPropertyImpl<>(null, setMethod);
    }

    @Nullable
    R get(T obj);

    @Nullable
    R set(T obj, R value);

    @Nonnull
    Property.PropertyType getPropertyType();

    default R apply(T obj, R value) {
        return set(obj, value);
    }

    default void accept(T obj, R value) {
        set(obj, value);
    }

    default Property<R> wrap(@Nonnull T obj) {
        return new BiPropertyWrapper<>(this, obj);
    }

    class DefaultPropertyImpl<T, R> implements BiProperty<T, R> {

        @Nullable
        private final Function<T, R> getMethod;

        @Nullable
        private final BiConsumer<T, R> setMethod;

        @Nonnull
        private final Property.PropertyType type;

        public DefaultPropertyImpl(@Nullable Function<T, R> getMethod, @Nullable BiConsumer<T, R> setMethod) {
            this.getMethod = getMethod;
            this.setMethod = setMethod;
            if (getMethod == null) {
                if (setMethod == null) {
                    throw new UnsupportedOperationException("'getMethod' and 'setMethod' cannot be both null");
                } else {
                    type = Property.PropertyType.WriteOnly;
                }
            } else {
                if (setMethod == null) {
                    type = Property.PropertyType.ReadOnly;
                } else {
                    type = Property.PropertyType.ReadWrite;
                }
            }
        }

        @Override
        @Nullable
        public R get(T obj) {
            Function<T, R> getMethod = this.getMethod;
            if (getMethod == null) {
                throw new UnsupportedOperationException("this property is write-only!");
            } else {
                return getMethod.apply(obj);
            }
        }

        @Override
        @Nullable
        public R set(T obj, R value) {
            BiConsumer<T, R> setMethod = this.setMethod;
            if (setMethod == null) {
                throw new UnsupportedOperationException("this property is read-only!");
            } else {
                setMethod.accept(obj, value);
                Function<T, R> getMethod = this.getMethod;
                if (getMethod == null)
                    return value;
                else
                    return getMethod.apply(obj);
            }
        }

        @Override
        @Nonnull
        public Property.PropertyType getPropertyType() {
            return type;
        }
    }

    class BiPropertyWrapper<T, R> implements Property<R> {

        @Nonnull
        private final T obj;

        @Nonnull
        private final BiProperty<T, R> baseProperty;

        BiPropertyWrapper(@Nonnull BiProperty<T, R> baseProperty, @Nonnull T obj) {
            this.baseProperty = baseProperty;
            this.obj = obj;
        }

        @Override
        @Nullable
        public R get() {
            return baseProperty.get(obj);
        }

        @Override
        @Nullable
        public R set(R value) {
            return baseProperty.set(obj, value);
        }

        @Override
        @Nonnull
        public PropertyType getPropertyType() {
            return baseProperty.getPropertyType();
        }
    }
}
