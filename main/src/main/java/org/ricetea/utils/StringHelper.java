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

    @Nonnull
    public static String replaceOnce(@Nonnull String original, @Nonnull String str, @Nonnull String replacement) {
        int length = str.length();
        if (length == 0)
            return original;
        int index = original.indexOf(str);
        if (index < 0)
            return original;
        if (index == 0)
            return replacement + original.substring(length);
        if (index == original.length() - length)
            return original.substring(0, index) + replacement;
        return original.substring(0, index) + replacement + original.substring(index + length);
    }
}
