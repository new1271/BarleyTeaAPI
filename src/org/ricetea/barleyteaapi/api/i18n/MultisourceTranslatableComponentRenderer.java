package org.ricetea.barleyteaapi.api.i18n;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.translation.Translator;

public class MultisourceTranslatableComponentRenderer extends TranslatableComponentRenderer<Locale> {

    private List<Translator> sources;

    public MultisourceTranslatableComponentRenderer() {
        sources = new ArrayList<Translator>();
    }

    @Override
    public @Nullable MessageFormat translate(final @Nonnull String key, final @Nonnull Locale context) {
        for (Translator source : sources) {
            MessageFormat format = source.translate(key, context);
            if (format != null)
                return format;
        }
        return null;
    }

    @Override
    protected @Nonnull Component renderTranslatable(final @Nonnull TranslatableComponent component,
            final @Nonnull Locale context) {
        Component translated = null;
        for (Translator source : sources) {
            translated = source.translate(component, context);
            if (translated != null)
                break;
        }
        if (translated != null)
            return translated;
        return super.renderTranslatable(component, context);
    }

    public void addSource(@Nullable Translator source) {
        if (source == null)
            return;
        if (!sources.contains(source))
            sources.add(source);
    }

    public void removeSource(@Nullable Translator source) {
        if (source == null)
            return;
        sources.removeIf(translator -> source.equals(translator));
    }
}
