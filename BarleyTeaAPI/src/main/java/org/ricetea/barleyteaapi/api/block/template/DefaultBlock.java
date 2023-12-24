package org.ricetea.barleyteaapi.api.block.template;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.barleyteaapi.api.helper.TranslationKeyHelper;

import javax.annotation.Nonnull;

public class DefaultBlock implements CustomBlock {

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
}
