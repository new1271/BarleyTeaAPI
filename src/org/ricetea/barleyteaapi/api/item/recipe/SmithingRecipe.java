package org.ricetea.barleyteaapi.api.item.recipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.internal.helper.SmithingHelper;

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
    public DataItemType getTemplateAsExample() {
        return template;
    }

    @Nonnull
    public DataItemType getAdditionAsExample() {
        return addition;
    }

    @Override
    public boolean filterAdditionType(@Nonnull DataItemType additionType) {
        return addition.equals(additionType);
    }

    @Override
    public boolean filterTemplateType(@Nonnull DataItemType templateType) {
        return template.equals(templateType);
    }

    @Nonnull
    public SmithingTransformRecipe toBukkitRecipe(NamespacedKey key) {
        return new SmithingTransformRecipe(key, new ItemStack(getResult().getMaterialBasedOn()),
                new MaterialChoice(getTemplateAsExample().getMaterialBasedOn()), 
                new MaterialChoice(getOriginal().getMaterialBasedOn()),
                new MaterialChoice(getAdditionAsExample().getMaterialBasedOn()), copyNbt);
    }

    @Nullable
    @Override
    public ItemStack apply(@Nonnull ItemStack original, @Nonnull ItemStack template, @Nonnull ItemStack addition) {
        ItemStack result = super.apply(original, template, addition);
        if (result != null && copyNbt) {
            result = SmithingHelper.copyNbt(this, original, result);
        }
        return result;
    }
}
