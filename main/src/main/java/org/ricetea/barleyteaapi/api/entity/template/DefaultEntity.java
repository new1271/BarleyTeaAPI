package org.ricetea.barleyteaapi.api.entity.template;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.api.helper.TranslationKeyHelper;

import javax.annotation.Nonnull;

public class DefaultEntity implements CustomEntity {

    @Nonnull
    private final NamespacedKey key;

    @Nonnull
    private final EntityType originalType;

    @Nonnull
    private final String translationKey;

    public DefaultEntity(@Nonnull NamespacedKey key, @Nonnull EntityType originalType) {
        this.key = key;
        this.originalType = originalType;
        this.translationKey = TranslationKeyHelper.getTranslationKey("entity", key);
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
    public EntityType getOriginalType() {
        return originalType;
    }

}
