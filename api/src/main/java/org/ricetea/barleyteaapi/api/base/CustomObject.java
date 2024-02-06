package org.ricetea.barleyteaapi.api.base;

import net.kyori.adventure.translation.Translatable;
import org.bukkit.Keyed;

import javax.annotation.Nonnull;

public interface CustomObject extends Keyed, Translatable {
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
}
