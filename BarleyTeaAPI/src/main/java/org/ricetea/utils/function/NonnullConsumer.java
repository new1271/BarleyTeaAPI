package org.ricetea.utils.function;

import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface NonnullConsumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t);

    /**
     * Returns a composed {@code NonnullConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code NonnullConsumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    @Nonnull
    default NonnullConsumer<T> andThen(@Nonnull Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> {
            if (Objects.nonNull(t)) {
                accept(t);
                after.accept(t);
            }
        };
    }

    /**
     * Returns a composed {@code NonnullConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code NonnullConsumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    @Nonnull
    default NonnullConsumer<T> andThen(@Nonnull NonnullConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> {
            if (Objects.nonNull(t)) {
                accept(t);
                after.accept(t);
            }
        };
    }

    @Nonnull
    default Consumer<T> toConsumer() {
        return (T t) -> accept(Objects.requireNonNull(t));
    }

    @Nonnull
    static <T> NonnullConsumer<T> fromConsumer(@Nonnull Consumer<T> consumer) {
        return (T t) -> consumer.accept(Objects.requireNonNull(t));
    }
}
