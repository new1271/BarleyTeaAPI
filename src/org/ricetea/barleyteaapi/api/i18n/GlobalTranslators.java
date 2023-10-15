package org.ricetea.barleyteaapi.api.i18n;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.ricetea.utils.Lazy;

import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;

public final class GlobalTranslators {
    @Nonnull
    private static final Lazy<GlobalTranslators> inst = Lazy.create(GlobalTranslators::new);
    private static final @Nonnull Lazy<MultisourceTranslatableComponentRenderer> lazyRenderer = Lazy.create(
            MultisourceTranslatableComponentRenderer::new);

    private GlobalTranslators() {
    }

    @Nonnull
    public static GlobalTranslators getInstance() {
        return inst.get();
    }

    public void addServerTranslationSource(@Nullable Translator source) {
        if (source == null)
            return;
        GlobalTranslator.translator().addSource(source);
    }

    public void addRenderTranslationSource(@Nullable Translator source) {
        if (source == null)
            return;
        lazyRenderer.get().addSource(source);
    }

    public void removeServerTranslationSource(@Nullable Translator source) {
        if (source == null)
            return;
        GlobalTranslator.translator().removeSource(source);
    }

    public void removeRenderTranslationSource(@Nullable Translator source) {
        if (source == null)
            return;
        MultisourceTranslatableComponentRenderer renderer = lazyRenderer.getUnsafe();
        if (renderer != null)
            renderer.removeSource(source);
    }

    public MultisourceTranslatableComponentRenderer getRenderer() {
        return lazyRenderer.get();
    }
}
