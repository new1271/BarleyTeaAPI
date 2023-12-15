package org.ricetea.barleyteaapi.api.item.registration;

import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.recipe.BaseCookingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.BlastingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.CampfireRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.FurnaceRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.SmokingRecipe;
import org.ricetea.utils.Lazy;

public final class CookingRecipeRegister extends RecipeRegister<BaseCookingRecipe> {

    @Nonnull
    private static final Lazy<CookingRecipeRegister> inst = Lazy.create(CookingRecipeRegister::new);

    private CookingRecipeRegister() {
        super("dummy_cooking_recipe");
    }

    @Nonnull
    public static CookingRecipeRegister getInstance() {
        BarleyTeaAPI.checkPluginUsable();
        return inst.get();
    }

    @Nullable
    public static CookingRecipeRegister getInstanceUnsafe() {
        return inst.getUnsafe();
    }

    @Override
    @Nullable
    protected NamespacedKey findDummyRecipeKey(@Nonnull BaseCookingRecipe recipe) {
        boolean isCampfireRecipe = recipe instanceof CampfireRecipe;
        boolean isSmokingRecipe = recipe instanceof SmokingRecipe;
        boolean isBlastingRecipe = recipe instanceof BlastingRecipe;
        ItemStack originalItem = new ItemStack(recipe.getOriginal().getMaterialBasedOn());
        for (var iterator = Bukkit.recipeIterator(); iterator.hasNext();) {
            Recipe iteratingRecipe = iterator.next();
            if (isCampfireRecipe) {
                if (iteratingRecipe instanceof org.bukkit.inventory.CampfireRecipe iteratingRecipe1) {
                    if (iteratingRecipe1.getInputChoice().test(originalItem)) {
                        return iteratingRecipe1.getKey();
                    }
                }
            } else {
                if (iteratingRecipe instanceof org.bukkit.inventory.FurnaceRecipe iteratingRecipe1) {
                    if (iteratingRecipe1.getInputChoice().test(originalItem)) {
                        return iteratingRecipe1.getKey();
                    }
                } else {
                    if (isSmokingRecipe) {
                        if (iteratingRecipe instanceof org.bukkit.inventory.SmokingRecipe iteratingRecipe1) {
                            if (iteratingRecipe1.getInputChoice().test(originalItem)) {
                                return iteratingRecipe1.getKey();
                            }
                        }
                    } else if (isBlastingRecipe) {
                        if (iteratingRecipe instanceof org.bukkit.inventory.BlastingRecipe iteratingRecipe1) {
                            if (iteratingRecipe1.getInputChoice().test(originalItem)) {
                                return iteratingRecipe1.getKey();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void afterRegisterRecipe(@Nonnull BaseCookingRecipe recipe) {
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                Logger logger = inst.getLogger();
                if (recipe instanceof CampfireRecipe) {
                    logger.info("registered " + recipe.getKey().toString() + " as campfire recipe!");
                } else if (recipe instanceof SmokingRecipe) {
                    logger.info("registered " + recipe.getKey().toString() + " as smoker recipe!");
                } else if (recipe instanceof BlastingRecipe) {
                    logger.info("registered " + recipe.getKey().toString() + " as blast-furnace recipe!");
                } else if (recipe instanceof FurnaceRecipe) {
                    logger.info("registered " + recipe.getKey().toString() + " as furnace recipe!");
                } else {
                    logger.info("registered " + recipe.getKey().toString() + " as unknown-type cooking recipe!");
                }
            }
        }
    }
}
