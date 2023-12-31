package org.ricetea.utils;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.StringJoiner;

public class StringHelper {

    @Nonnull
    public static String join(@Nonnull CharSequence delimiter, @Nonnull Object... args) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (Object arg : args)
            joiner.add(Objects.toString(arg));
        return joiner.toString();
    }

    @Nonnull
    public static String joinWithoutNull(@Nonnull CharSequence delimiter, @Nonnull Object... args) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (Object arg : args) {
            if (arg != null)
                joiner.add(arg.toString());
        }
        return joiner.toString();
    }
}
