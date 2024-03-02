package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class Either<L, R> implements EitherOperation<L, R> {

    @Nullable
    final L left;
    @Nullable
    final R right;

    protected Either(@Nullable L left, @Nullable R right) {
        this.left = left;
        this.right = right;
    }

    @Nonnull
    public static <L, R> Either<L, R> left(@Nonnull L left) {
        return new Either<>(left, null);
    }

    @Nonnull
    public static <L, R> Either<L, R> right(@Nonnull R right) {
        return new Either<>(null, right);
    }

    @Nullable
    public L left() {
        return left;
    }

    @Nullable
    public R right() {
        return right;
    }

    public boolean isLeft() {
        return left != null;
    }

    public boolean isRight() {
        return right != null;
    }

    public boolean isEmpty() {
        return left == null && right == null;
    }

    public void call(@Nullable Consumer<L> consumerForLeft, @Nullable Consumer<R> consumerForRight) {
        L left = this.left;
        R right = this.right;
        if (left != null && consumerForLeft != null)
            consumerForLeft.accept(left);
        if (right != null && consumerForRight != null)
            consumerForRight.accept(right);
    }

    public <T> void call(@Nullable BiConsumer<L, T> consumerForLeft, @Nullable BiConsumer<R, T> consumerForRight,
                         @Nullable T extra) {
        L left = this.left;
        R right = this.right;
        if (left != null && consumerForLeft != null)
            consumerForLeft.accept(left, extra);
        if (right != null && consumerForRight != null)
            consumerForRight.accept(right, extra);
    }

    @Nullable
    public <T> T map(@Nullable Function<L, T> mapFunctionForLeft,
                     @Nullable Function<R, T> mapFunctionForRight) {
        L left = this.left;
        R right = this.right;
        if (left != null && mapFunctionForLeft != null)
            return mapFunctionForLeft.apply(left);
        if (right != null && mapFunctionForRight != null)
            return mapFunctionForRight.apply(right);
        return null;
    }

    @Nullable
    public <T, TReturn> TReturn map(@Nullable BiFunction<L, T, TReturn> mapFunctionForLeft,
                                    @Nullable BiFunction<R, T, TReturn> mapFunctionForRight, @Nullable T extra) {
        L left = this.left;
        R right = this.right;
        if (left != null && mapFunctionForLeft != null)
            return mapFunctionForLeft.apply(left, extra);
        if (right != null && mapFunctionForRight != null)
            return mapFunctionForRight.apply(right, extra);
        return null;
    }

    @Nonnull
    public <T> T nonNullMap(@Nonnull Function<L, T> mapFunctionForLeft,
                            @Nonnull Function<R, T> mapFunctionForRight) {
        L left = this.left;
        R right = this.right;
        if (left != null)
            return mapFunctionForLeft.apply(left);
        if (right != null)
            return mapFunctionForRight.apply(right);
        throw new NullPointerException();
    }

    public boolean equals(Object another) {
        if (!super.equals(another) && another instanceof Either<?, ?> either) {
            L left = left();
            if (left != null)
                return Objects.equals(left,either.left());
            R right = right();
            if (right != null)
                return Objects.equals(right, either.right());
            return either.isEmpty();
        }
        return true;
    }
}
