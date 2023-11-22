package org.ricetea.barleyteaapi.api.item.recipe;

import javax.annotation.Nonnull;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;

public abstract class BaseRecipe implements Keyed {
    @Nonnull
    private final NamespacedKey key;
    @Nonnull
    private final DataItemType result;

    public BaseRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType result) throws UnsupportedOperationException {
        this.key = key;
        result.processLeftOrRight(left -> {
            if (left.isAir()) {
                throw new UnsupportedOperationException(
                        "'result' cannot be an air!");
            }
        }, right -> {
            if (!(right instanceof FeatureItemGive)) {
                throw new UnsupportedOperationException(
                        "'result' must implement FeatureItemGive interface!");
            }
        });
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

    @Nonnull
    public abstract Recipe toBukkitRecipe(@Nonnull NamespacedKey key);
}
