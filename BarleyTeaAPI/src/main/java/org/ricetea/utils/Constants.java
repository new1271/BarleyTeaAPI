package org.ricetea.utils;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.IntUnaryOperator;

public class Constants {
    @Nonnull
    public static final UUID EMPTY_UUID = new UUID(0, 0);

    @Nonnull
    public static final IntUnaryOperator IncreaseOperator = i -> i + 1;
    @Nonnull
    public static final IntUnaryOperator DecreaseOperator = i -> i - 1;
}
