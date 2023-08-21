package org.ricetea.barleyteaapi.api.item.recipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public abstract class BaseSmithingRecipe extends BaseRecipe implements SmithingFunction {

    @Nonnull
    private final DataItemType original;

    public BaseSmithingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType original,
            @Nonnull DataItemType result) throws UnsupportedOperationException {
        super(key, result);
        this.original = original;
    }

    @Nonnull
    public DataItemType getOriginal() {
        return original;
    }

    public abstract boolean filterAdditionType(@Nonnull DataItemType additionType);

    public abstract boolean filterTemplateType(@Nonnull DataItemType templateType);

    @Nullable
    public ItemStack apply(@Nonnull ItemStack original, @Nonnull ItemStack template, @Nonnull ItemStack addition) {
        return getResult().mapLeftOrRight(ItemStack::new, right -> {
            return ObjectUtil.mapWhenNonnull(ObjectUtil.tryCast(right, FeatureItemGive.class),
                    itemGiveFeature -> itemGiveFeature.handleItemGive(1));
        });
    }
}