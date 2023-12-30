package org.ricetea.barleyteaapi.api.item.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.internal.helper.SmithingHelper;
import org.ricetea.utils.CollectionUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class SmithingRecipe extends BaseSmithingRecipe {

    private final boolean copyNbt;

    @Nonnull
    private final CustomItemType template, addition;

    public SmithingRecipe(@Nonnull NamespacedKey key, @Nonnull CustomItemType original, @Nonnull CustomItemType template,
                          @Nonnull CustomItemType addition, @Nonnull CustomItemType result) {
        this(key, original, template, addition, result, true);
    }

    public SmithingRecipe(@Nonnull NamespacedKey key, @Nonnull CustomItemType original, @Nonnull CustomItemType template,
                          @Nonnull CustomItemType addition, @Nonnull CustomItemType result, boolean copyNbt) {
        super(key, original, result);
        this.template = template;
        this.addition = addition;
        this.copyNbt = copyNbt;
    }

    @Nonnull
    public Set<CustomItemType> getTemplates() {
        return Objects.requireNonNull(Collections.singleton(template));
    }

    @Nonnull
    public Set<CustomItemType> getAdditions() {
        return Objects.requireNonNull(Collections.singleton(addition));
    }

    @Nonnull
    public SmithingTransformRecipe toBukkitRecipe(@Nonnull NamespacedKey key) {
        return new SmithingTransformRecipe(key, new ItemStack(getResult().getOriginalType()),
                new MaterialChoice(CollectionUtil.firstOrDefault(getTemplates(), CustomItemType.empty())
                        .getOriginalType()),
                new MaterialChoice(getOriginal().getOriginalType()),
                new MaterialChoice(CollectionUtil.firstOrDefault(getAdditions(), CustomItemType.empty())
                        .getOriginalType()),
                copyNbt);
    }

    @Nullable
    @Override
    public ItemStack apply(@Nonnull ItemStack original, @Nonnull ItemStack template, @Nonnull ItemStack addition) {
        ItemStack result;
        if (copyNbt) {
            if (getOriginal() == getResult()) {
                result = original.clone();
            } else {
                result = SmithingHelper.copyNbt(this, original, super.apply(original, template, addition));
            }
        } else {
            result = super.apply(original, template, addition);
        }
        return result;
    }
}
