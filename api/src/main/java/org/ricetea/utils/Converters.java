package org.ricetea.utils;

import javax.annotation.Nonnull;
import java.text.MessageFormat;

public final class Converters {

    public static @Nonnull String toStringFormat(@Nonnull MessageFormat format) {
        format = (MessageFormat) format.clone();
        int length = format.getFormats().length;
        if (length > 1) {
            for (int i = 0; i < length; i++) {
                format.setFormat(i, null);
            }
            String result = format.toPattern();
            for (int i = 0; i < length; i++) {
                result = result.replace("{" + i + "}", "%" + (i + 1) + "$s");
            }
            return result;
        } else if (length == 1) {
            format.setFormat(0, null);
            return format.toPattern().replace("{0}", "%s");
        } else {
            return format.toPattern();
        }
    }

    @SuppressWarnings("StringEquality")
    @Nonnull
    public static MessageFormat toMessageFormat(@Nonnull String format) {
        final String oldFormat = format;
        final int oldFormatLength = oldFormat.length();
        boolean isSequential = format.contains("%s");
        int i = 0;
        int lastFormatLength;
        int newFormatLength = oldFormatLength;
        if (isSequential) {
            do {
                lastFormatLength = newFormatLength;
                format = StringHelper.replaceOnce(format, "%s", "{" + i + "}");
                newFormatLength = format.length();
                i++;
            } while (format != oldFormat && newFormatLength != lastFormatLength);
        } else {
            do {
                lastFormatLength = newFormatLength;
                format = format.replace("%" + Integer.toString(i + 1) + "$s", "{" + i + "}");
                newFormatLength = format.length();
                i++;
            } while (format != oldFormat && newFormatLength != lastFormatLength);
        }
        return new MessageFormat(format.replace("'", "’"));
    }
}
