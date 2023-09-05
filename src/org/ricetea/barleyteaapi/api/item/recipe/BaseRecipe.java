package org.ricetea.barleyteaapi.api.item.recipe;

import javax.annotation.Nonnull;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;

public abstract class BaseRecipe implements Keyed {
    @Nonnull
    private final NamespacedKey key;
    @Nonnull
    private final DataItemType result;

    public BaseRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType result) throws UnsupportedOperationException {
        this.key = key;
        if (result.mapLeftOrRight(Material::isAir, dt -> (Boolean) !(dt instanceof FeatureItemGive)) != true) {
            throw new UnsupportedOperationException(
                    "if 'result' is custom item, it must implement FeatureItemGive interface!");
        }
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
