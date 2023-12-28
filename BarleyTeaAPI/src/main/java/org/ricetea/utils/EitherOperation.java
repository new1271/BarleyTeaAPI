package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public interface EitherOperation<L, R> {

    @Nonnull
    default <T> T nonNullMap(@Nonnull Function<L, T> leftFunction, @Nonnull Function<R, T> rightFunction) {
        return Objects.requireNonNull(map(leftFunction, rightFunction));
    }

    @Nullable
    <T> T map(@Nonnull Function<L, T> leftFunction, @Nonnull Function<R, T> rightFunction);

    void call(@Nonnull Consumer<L> leftConsumer, @Nonnull Consumer<R> rightConsumer);
}
