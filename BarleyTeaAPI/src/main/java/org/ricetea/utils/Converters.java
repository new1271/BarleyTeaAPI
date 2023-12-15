package org.ricetea.utils;

import java.text.MessageFormat;

import javax.annotation.Nonnull;

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
}
