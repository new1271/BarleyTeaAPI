package org.ricetea.barleyteaapi.api.item.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public abstract class BaseSmithingRecipe extends BaseRecipe implements SmithingFunction {

    @Nonnull
    private final CustomItemType original;

    public BaseSmithingRecipe(@Nonnull NamespacedKey key, @Nonnull CustomItemType original,
                              @Nonnull CustomItemType result) {
        super(key, result);
        this.original = original;
    }

    @Nonnull
    public CustomItemType getOriginal() {
        return original;
    }

    @Nonnull
    public abstract Set<CustomItemType> getTemplates();

    @Nonnull
    public abstract Set<CustomItemType> getAdditions();

    public boolean filterTemplateType(@Nonnull CustomItemType templateType) {
        return getTemplates().contains(templateType);
    }

    public boolean filterAdditionType(@Nonnull CustomItemType additionType) {
        return getAdditions().contains(additionType);
    }

    @Nullable
    public ItemStack apply(@Nonnull ItemStack original, @Nonnull ItemStack template, @Nonnull ItemStack addition) {
        return getResult().map(ItemStack::new, right ->
                ObjectUtil.safeMap(ObjectUtil.tryCast(right, FeatureItemGive.class),
                        itemGiveFeature -> itemGiveFeature.handleItemGive(1))
        );
    }
}