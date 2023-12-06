package org.ricetea.barleyteaapi.api.item.recipe;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.utils.ObjectUtil;

public abstract class BaseSmithingRecipe extends BaseRecipe implements SmithingFunction {

    @Nonnull
    private final DataItemType original;

    public BaseSmithingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType original,
            @Nonnull DataItemType result) {
        super(key, result);
        this.original = original;
    }

    @Nonnull
    public DataItemType getOriginal() {
        return original;
    }

    @Nonnull
    public abstract Set<DataItemType> getTemplates();

    @Nonnull
    public abstract Set<DataItemType> getAdditions();

    public boolean filterTemplateType(@Nonnull DataItemType templateType) {
        return getTemplates().contains(templateType);
    }

    public boolean filterAdditionType(@Nonnull DataItemType additionType) {
        return getAdditions().contains(additionType);
    }

    @Nullable
    public ItemStack apply(@Nonnull ItemStack original, @Nonnull ItemStack template, @Nonnull ItemStack addition) {
        return getResult().mapLeftOrRight(ItemStack::new, right -> {
            return ObjectUtil.mapWhenNonnull(ObjectUtil.tryCast(right, FeatureItemGive.class),
                    itemGiveFeature -> itemGiveFeature.handleItemGive(1));
        });
    }
}