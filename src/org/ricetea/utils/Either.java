package org.ricetea.utils;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Either<L, R> {

    @Nullable
    final L left;
    @Nullable
    final R right;

    protected Either(L left, R right) {
        this.left = left;
        this.right = right;
    }

    @Nonnull
    public static <L, R> Either<L, R> left(L left) {
        return new Either<L, R>(left, null);
    }

    @Nonnull
    public static <L, R> Either<L, R> right(R right) {
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

    public void processLeftOrRight(@Nonnull Consumer<L> consumerForLeft, @Nonnull Consumer<R> consumerForRight) {
        L left = this.left;
        R right = this.right;
        if (left != null)
            consumerForLeft.accept(left);
        if (right != null)
            consumerForRight.accept(right);
    }

    @Nullable
    public <T> T mapLeftOrRight(@Nonnull Function<L, T> mapFunctionForLeft,
            @Nonnull Function<R, T> mapFunctionForRight) {
        L left = this.left;
        R right = this.right;
        if (left != null)
            return mapFunctionForLeft.apply(left);
        if (right != null)
            return mapFunctionForRight.apply(right);
        return null;
    }

    public boolean equals(Object another) {
        if (another instanceof Either<?, ?> either) {
            L left = left();
            R right = right();
            Object anotherLeft = either.left();
            Object anotherRight = either.right();
            if (left != null && anotherLeft != null) {
                return left.equals(anotherLeft);
            } else if (right != null && anotherRight != null) {
                return right.equals(anotherRight);
            } else {
                return isEmpty() && either.isEmpty();
            }
        }
        return super.equals(another);
    }
}
