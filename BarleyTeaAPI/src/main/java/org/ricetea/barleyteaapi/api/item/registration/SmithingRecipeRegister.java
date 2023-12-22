package org.ricetea.barleyteaapi.api.item.registration;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.recipe.ArmorTrimSmithingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.BaseSmithingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.SmithingRecipe;
import org.ricetea.utils.CollectionUtil;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.logging.Logger;

public final class SmithingRecipeRegister extends RecipeRegister<BaseSmithingRecipe> {

    @Nonnull
    private static final Lazy<SmithingRecipeRegister> inst = Lazy.create(SmithingRecipeRegister::new);

    private SmithingRecipeRegister() {
        super("dummy_smithing_recipe");
    }

    @Nonnull
    public static SmithingRecipeRegister getInstance() {
        BarleyTeaAPI.checkPluginUsable();
        return inst.get();
    }

    @Nullable
    public static SmithingRecipeRegister getInstanceUnsafe() {
        return inst.getUnsafe();
    }

    @Override
    @Nullable
    protected NamespacedKey findDummyRecipeKey(@Nonnull BaseSmithingRecipe recipe) {
        ItemStack originalItem = new ItemStack(recipe.getOriginal().getMaterialBasedOn());
        ItemStack templateItem = new ItemStack(
                CollectionUtil.firstOrDefault(recipe.getTemplates(), DataItemType.empty())
                        .getMaterialBasedOn());
        ItemStack additionItem = new ItemStack(
                CollectionUtil.firstOrDefault(recipe.getAdditions(), DataItemType.empty())
                        .getMaterialBasedOn());
        for (var iterator = Bukkit.recipeIterator(); iterator.hasNext();) {
            Recipe iteratingRecipe = iterator.next();
            if (iteratingRecipe instanceof SmithingTransformRecipe iteratingSmithingRecipe) {
                if (iteratingSmithingRecipe.getBase().test(originalItem)
                        && iteratingSmithingRecipe.getTemplate().test(templateItem)
                        && iteratingSmithingRecipe.getAddition().test(additionItem)) {
                    return iteratingSmithingRecipe.getKey();
                }
            } else if (iteratingRecipe instanceof SmithingTrimRecipe iteratingSmithingRecipe) {
                if (iteratingSmithingRecipe.getBase().test(originalItem)
                        && iteratingSmithingRecipe.getTemplate().test(templateItem)
                        && iteratingSmithingRecipe.getAddition().test(additionItem)) {
                    return iteratingSmithingRecipe.getKey();
                }
            }
        }
        return null;
    }

    @Override
    protected void afterRegisterRecipe(@Nonnull BaseSmithingRecipe recipe) {
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                Logger logger = inst.getLogger();
                if (recipe instanceof SmithingRecipe) {
                    logger.info("registered " + recipe.getKey() + " as normal smithing recipe!");
                } else if (recipe instanceof ArmorTrimSmithingRecipe) {
                    logger.info("registered " + recipe.getKey() + " as armor-trimming smithing recipe!");
                } else {
                    logger.info("registered " + recipe.getKey() + " as unknown-type smithing recipe!");
                }
            }
        }
    }
}
