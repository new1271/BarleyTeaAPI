package org.ricetea.barleyteaapi.api.item.recipe;

import javax.annotation.Nonnull;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

public abstract class BaseRecipe implements Keyed {
    @Nonnull
    private final NamespacedKey key;

    public BaseRecipe(@Nonnull NamespacedKey key) {
        this.key = key;
    }

    @Nonnull
    public NamespacedKey getKey() {
        return key;
    }

}
