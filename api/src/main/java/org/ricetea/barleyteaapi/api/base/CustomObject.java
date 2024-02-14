package org.ricetea.barleyteaapi.api.base;

import net.kyori.adventure.translation.Translatable;
import org.bukkit.Keyed;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public interface CustomObject<F extends Feature> extends Keyed, Translatable {
    @Nonnull
    @Override
    default String translationKey() {
        return getTranslationKey();
    }

    @Nonnull
    String getTranslationKey();

    @Nonnull
    default String getDefaultName() {
        return getTranslationKey();
    }

    @Nullable
    <T extends F> T getFeature(@Nonnull Class<T> featureClass);

    @Nonnull
    Collection<Class<? extends F>> getFeatures();
}
