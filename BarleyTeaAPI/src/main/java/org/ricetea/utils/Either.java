package org.ricetea.utils;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.ricetea.utils.function.NonnullFunction;

public class Either<L, R> {

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
        return new Either<L, R>(left, null);
    }

    @Nonnull
    public static <L, R> Either<L, R> right(@Nonnull R right) {
        return new Either<L, R>(null, right);
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
    public <T> T nonNullMap(@Nonnull NonnullFunction<L, T> mapFunctionForLeft,
            @Nonnull NonnullFunction<R, T> mapFunctionForRight) {
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
            return Objects.equals(left(), either.left()) &&
                    Objects.equals(right(), either.right());
        }
        return true;
    }
}
