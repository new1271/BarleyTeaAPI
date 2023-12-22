package org.ricetea.barleyteaapi.api.item.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
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
    private final DataItemType template, addition;

    public SmithingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType original, @Nonnull DataItemType template,
            @Nonnull DataItemType addition, @Nonnull DataItemType result) {
        this(key, original, template, addition, result, true);
    }

    public SmithingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType original, @Nonnull DataItemType template,
            @Nonnull DataItemType addition, @Nonnull DataItemType result, boolean copyNbt) {
        super(key, original, result);
        this.template = template;
        this.addition = addition;
        this.copyNbt = copyNbt;
    }

    @Nonnull
    public Set<DataItemType> getTemplates() {
        return Objects.requireNonNull(Collections.singleton(template));
    }

    @Nonnull
    public Set<DataItemType> getAdditions() {
        return Objects.requireNonNull(Collections.singleton(addition));
    }

    @Nonnull
    public SmithingTransformRecipe toBukkitRecipe(@Nonnull NamespacedKey key) {
        return new SmithingTransformRecipe(key, new ItemStack(getResult().getMaterialBasedOn()),
                new MaterialChoice(CollectionUtil.firstOrDefault(getTemplates(), DataItemType.empty())
                        .getMaterialBasedOn()),
                new MaterialChoice(getOriginal().getMaterialBasedOn()),
                new MaterialChoice(CollectionUtil.firstOrDefault(getAdditions(), DataItemType.empty())
                        .getMaterialBasedOn()),
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
