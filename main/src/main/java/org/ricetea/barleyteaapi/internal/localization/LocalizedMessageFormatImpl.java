package org.ricetea.barleyteaapi.internal.localization;

import org.ricetea.barleyteaapi.api.localization.LocalizedMessageFormat;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.text.MessageFormat;
import java.util.*;

public class LocalizedMessageFormatImpl implements LocalizedMessageFormat {
    @Nonnull
    private final Map<Locale, MessageFormat> formatMap = new HashMap<>(4);
    @Nonnull
    private final String translationKey;
    @Nonnull
    private final Lazy<MessageFormat> defaultFormat = Lazy.createThreadSafe(() -> new MessageFormat(getTranslationKey()));

    private LocalizedMessageFormatImpl(@Nonnull String translationKey) {
        this.translationKey = translationKey;
    }

    @Nonnull
    public static LocalizedMessageFormatImpl create(@Nonnull String translationKey) {
        return new LocalizedMessageFormatImpl(translationKey);
    }

    @Nonnull
    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Nonnull
    @Override
    public MessageFormat getFormat(@Nonnull Locale locale) {
        MessageFormat format = formatMap.get(locale);
        if (format == null)
            return defaultFormat.get();
        return format;
    }

    @Override
    public void setFormat(@Nonnull Locale locale, @Nonnull MessageFormat format) {
        formatMap.put(locale, format);
    }

    @Override
    public void removeFormat(@Nonnull Locale locale) {
        formatMap.remove(locale);
    }

    @Nonnull
    @Override
    public Collection<Locale> getLocales() {
        return Set.copyOf(formatMap.keySet());
    }
}
