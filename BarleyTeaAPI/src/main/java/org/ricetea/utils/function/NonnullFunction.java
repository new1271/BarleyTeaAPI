package org.ricetea.utils.function;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface NonnullFunction<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t);

    /**
     * Returns a composed function that first applies the {@code before}
     * function to its input, and then applies this function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <V> the type of input to the {@code before} function, and to the
     *           composed function
     * @param before the function to apply before this function is applied
     * @return a composed function that first applies the {@code before}
     * function and then applies this function
     * @throws NullPointerException if before is null
     *
     * @see #andThen(Function)
     */
    @Nonnull
    default <V> NonnullFunction<V, R> compose(Function<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(Objects.requireNonNull(before.apply(v)));
    }

    /**
     * Returns a composed function that first applies the {@code before}
     * function to its input, and then applies this function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <V> the type of input to the {@code before} function, and to the
     *           composed function
     * @param before the function to apply before this function is applied
     * @return a composed function that first applies the {@code before}
     * function and then applies this function
     * @throws NullPointerException if before is null
     *
     * @see #andThen(NonnullFunction)
     */
    @Nonnull
    default <V> NonnullFunction<V, R> compose(NonnullFunction<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> {
            Objects.requireNonNull(v);
            return apply(Objects.requireNonNull(before.apply(v)));
        };
    }

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <V> the type of output of the {@code after} function, and of the
     *           composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then
     * applies the {@code after} function
     * @throws NullPointerException if after is null
     *
     * @see #compose(Function)
     */
    @Nonnull
    default <V> NonnullFunction<T, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(Objects.requireNonNull(t)));
    }

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <V> the type of output of the {@code after} function, and of the
     *           composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then
     * applies the {@code after} function
     * @throws NullPointerException if after is null
     *
     * @see #compose(Function)
     */

    @SuppressWarnings("null")
    @Nonnull
    default <V> NonnullFunction<T, V> andThen(NonnullFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t) -> {
            var r = apply(Objects.requireNonNull(t));
            Objects.requireNonNull(r);
            return after.apply(r);
        };
    }

    @Nonnull
    default Function<T, R> toFunction() {
        return (T t) -> apply(Objects.requireNonNull(t));
    }

    @Nonnull
    static <T, R> NonnullFunction<T, R> fromFunction(@Nonnull Function<T, R> function) {
        return (T t) -> function.apply(Objects.requireNonNull(t));
    }

    /**
     * Returns a function that always returns its input argument.
     *
     * @param <T> the type of the input and output objects to the function
     * @return a function that always returns its input argument
     */
    @Nonnull
    static <T> NonnullFunction<T, T> identity() {
        return t -> t;
    }
}
