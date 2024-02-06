package org.ricetea.barleyteaapi.api.localization;

import net.kyori.adventure.translation.Translatable;
import org.ricetea.barleyteaapi.api.internal.localization.LocalizedMessageFormatImpl;
import org.ricetea.utils.CollectionUtil;

import javax.annotation.Nonnull;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.function.Predicate;

public interface LocalizedMessageFormat extends Translatable {

    @Nonnull
    Locale DEFAULT_LOCALE = Locale.US;

    @Nonnull
    static LocalizedMessageFormat create(@Nonnull String translationKey) {
        return LocalizedMessageFormatImpl.create(translationKey);
    }

    @Nonnull
    String getTranslationKey();

    @Override
    @Nonnull
    default String translationKey() {
        return getTranslationKey();
    }

    @Nonnull
    default MessageFormat getFormat() {
        Collection<Locale> locales = getLocales();
        Locale locale = locales.stream()
                .filter(Predicate.isEqual(DEFAULT_LOCALE))
                .findAny()
                .orElse(CollectionUtil.first(locales));
        if (locale == null)
            return new MessageFormat(getTranslationKey());
        return getFormat(locale);
    }

    default void setFormat(@Nonnull MessageFormat format) {
        setFormat(DEFAULT_LOCALE, format);
    }

    @Nonnull
    MessageFormat getFormat(@Nonnull Locale locale);

    void setFormat(@Nonnull Locale locale, @Nonnull MessageFormat format);

    void removeFormat(@Nonnull Locale locale);

    @Nonnull
    Collection<Locale> getLocales();
}
