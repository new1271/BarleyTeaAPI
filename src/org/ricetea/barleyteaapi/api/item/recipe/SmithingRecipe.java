package org.ricetea.barleyteaapi.api.item.recipe;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;

public class SmithingRecipe extends BaseSmithingRecipe {

    @Nonnull
    private final DataItemType template, addition;

    public SmithingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType original, @Nonnull DataItemType template,
            @Nonnull DataItemType addition, @Nonnull DataItemType result) throws UnsupportedOperationException {
        super(key, original, result);
        this.template = template;
        this.addition = addition;
    }

    @Nonnull
    public DataItemType getTemplate() {
        return template;
    }

    @Nonnull
    public DataItemType getAddition() {
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
        return new SmithingTransformRecipe(key, new ItemStack(getResult().toMaterial()),
                new MaterialChoice(getTemplate().toMaterial()), new MaterialChoice(getOriginal().toMaterial()),
                new MaterialChoice(getAddition().toMaterial()), true);
    }
}
