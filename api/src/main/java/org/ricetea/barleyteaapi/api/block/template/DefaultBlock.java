package org.ricetea.barleyteaapi.api.block.template;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.barleyteaapi.api.block.feature.BlockFeature;
import org.ricetea.barleyteaapi.api.helper.TranslationKeyHelper;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.WeakHashMap;

public class DefaultBlock implements CustomBlock {

    @Nonnull
    private final Lazy<Collection<Class<? extends BlockFeature>>> featuresLazy =
            Lazy.createThreadSafe(CustomBlock.super::getFeatures);

    @Nonnull
    private final NamespacedKey key;

    @Nonnull
    private final Material originalType;

    @Nonnull
    private final String translationKey;

    public DefaultBlock(@Nonnull NamespacedKey key, @Nonnull Material originalType) {
        this.key = key;
        this.originalType = originalType;
        this.translationKey = TranslationKeyHelper.getTranslationKey("block", key, null);
    }

    @Nonnull
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Nonnull
    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Nonnull
    @Override
    public Material getOriginalType() {
        return originalType;
    }

    @Nonnull
    @Override
    public Collection<Class<? extends BlockFeature>> getFeatures() {
        return featuresLazy.get();
    }
}
