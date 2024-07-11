package org.ricetea.barleyteaapi.api.entity.template;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.api.entity.feature.EntityFeature;
import org.ricetea.barleyteaapi.api.helper.TranslationKeyHelper;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

public class DefaultEntity implements CustomEntity {
    @Nonnull
    private final Lazy<Collection<Class<? extends EntityFeature>>> featuresLazy =
            Lazy.createThreadSafe(CustomEntity.super::getFeatures);

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

    @Nonnull
    @Override
    public Collection<Class<? extends EntityFeature>> getFeatures() {
        return featuresLazy.get();
    }
}
