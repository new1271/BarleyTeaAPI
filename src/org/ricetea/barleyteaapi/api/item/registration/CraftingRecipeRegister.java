package org.ricetea.barleyteaapi.api.item.registration;

import java.util.*;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.recipe.BaseCraftingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.ShapedCraftingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.ShapelessCraftingRecipe;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

public final class CraftingRecipeRegister extends RecipeRegister<BaseCraftingRecipe> {

    @Nonnull
    private static final Lazy<CraftingRecipeRegister> inst = Lazy.create(CraftingRecipeRegister::new);

    private CraftingRecipeRegister() {
        super("dummy_crafting_recipe");
    }

    @Nonnull
    public static CraftingRecipeRegister getInstance() {
        BarleyTeaAPI.checkPluginUsable();
        return inst.get();
    }

    @Nullable
    public static CraftingRecipeRegister getInstanceUnsafe() {
        return inst.getUnsafe();
    }

    @Override
    @Nullable
    protected NamespacedKey findDummyRecipeKey(@Nonnull BaseCraftingRecipe recipe) {
        List<DataItemType> ingredients;
        if (recipe instanceof ShapedCraftingRecipe shapedRecipe) {
            ingredients = shapedRecipe.getIngredientMatrix();
        } else if (recipe instanceof ShapelessCraftingRecipe shapelessRecipe) {
            ingredients = shapelessRecipe.getIngredients();
        } else {
            return null;
        }
        ItemStack[] testingItemStacks = ingredients.stream()
                .map(DataItemType::getMaterialBasedOn)
                .map(ItemStack::new)
                .toArray(ItemStack[]::new);
        if (testingItemStacks.length < 9) {
            testingItemStacks = Arrays.copyOf(testingItemStacks, 9);
        }
        return ObjectUtil.mapWhenNonnull(
                ObjectUtil.cast(Bukkit.getCraftingRecipe(testingItemStacks, Bukkit.getWorlds().get(0)), Keyed.class),
                Keyed::getKey);
    }

    @Override
    protected void afterRegisterRecipe(@Nonnull BaseCraftingRecipe recipe) {
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                Logger logger = inst.getLogger();
                if (recipe instanceof ShapedCraftingRecipe) {
                    logger.info("registered " + recipe.getKey().toString() + " as shaped crafting recipe!");
                } else if (recipe instanceof ShapelessCraftingRecipe) {
                    logger.info("registered " + recipe.getKey().toString() + " as shapeless crafting recipe!");
                } else {
                    logger.info("registered " + recipe.getKey().toString() + " as unknown-type crafting recipe!");
                }
            }
        }
    }
}
