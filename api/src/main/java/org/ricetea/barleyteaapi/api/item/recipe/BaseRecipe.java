package org.ricetea.barleyteaapi.api.item.recipe;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.ricetea.barleyteaapi.api.helper.FeatureHelper;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;

import javax.annotation.Nonnull;

public abstract class BaseRecipe implements Keyed {
    @Nonnull
    private final NamespacedKey key;
    @Nonnull
    private final CustomItemType result;

    public BaseRecipe(@Nonnull NamespacedKey key, @Nonnull CustomItemType result) throws UnsupportedOperationException {
        this.key = key;
        result.call(left -> {
            if (left.isAir()) {
                throw new UnsupportedOperationException(
                        "'result' cannot be an air!");
            }
        }, right -> {
            if (!FeatureHelper.hasFeature(right, FeatureItemGive.class)) {
                throw new UnsupportedOperationException(
                        "'result' must has FeatureItemGive feature!");
            }
        });
        this.result = result;
    }

    @Nonnull
    public NamespacedKey getKey() {
        return key;
    }

    @Nonnull
    public CustomItemType getResult() {
        return result;
    }

    @Nonnull
    public abstract Recipe toBukkitRecipe(@Nonnull NamespacedKey key);
}
