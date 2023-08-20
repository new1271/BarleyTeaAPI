package org.ricetea.barleyteaapi.api.item.recipe;

import javax.annotation.Nonnull;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;

public abstract class BaseRecipe implements Keyed {
    @Nonnull
    private final NamespacedKey key;
    @Nonnull
    private final DataItemType result;

    public BaseRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType result) {
        this.key = key;
        this.result = result;
    }

    @Nonnull
    public NamespacedKey getKey() {
        return key;
    }

    @Nonnull
    public DataItemType getResult() {
        return result;
    }
}
