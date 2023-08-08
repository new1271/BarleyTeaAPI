package org.ricetea.barleyteaapi.util;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Either<L, R> {

    @Nullable
    final L left;
    @Nullable
    final R right;

    private Either(L left, R right) {
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

    public boolean hasLeft() {
        return left != null;
    }

    public boolean hasRight() {
        return right != null;
    }

    public void processLeftOrRight(@Nonnull Consumer<L> consumerForLeft, @Nonnull Consumer<R> consumerForRight) {
        L left = this.left;
        R right = this.right;
        if (left != null)
            consumerForLeft.accept(left);
        if (right != null)
            consumerForRight.accept(right);
    }
}
