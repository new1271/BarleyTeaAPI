package org.ricetea.barleyteaapi.internal.connector.patch;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.Translator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

@FunctionalInterface
public interface ApplyTranslateFallbacksFunction {
    @Nullable
    Component apply(@Nonnull Translator translator, @Nullable Component component,
                    @Nonnull Locale locale);
}
