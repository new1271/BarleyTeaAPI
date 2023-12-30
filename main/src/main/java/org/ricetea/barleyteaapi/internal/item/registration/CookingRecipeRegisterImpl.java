package org.ricetea.barleyteaapi.internal.item.registration;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.recipe.*;
import org.ricetea.barleyteaapi.api.item.registration.CookingRecipeRegister;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.logging.Logger;

@Singleton
@ApiStatus.Internal
public final class CookingRecipeRegisterImpl extends BaseRecipeRegisterImpl<BaseCookingRecipe> implements CookingRecipeRegister {

    @Nonnull
    private static final Lazy<CookingRecipeRegisterImpl> inst = Lazy.create(CookingRecipeRegisterImpl::new);

    private CookingRecipeRegisterImpl() {
        super("dummy_cooking_recipe");
    }

    @Nonnull
    public static CookingRecipeRegisterImpl getInstance() {
        return inst.get();
    }

    @Nullable
    public static CookingRecipeRegisterImpl getInstanceUnsafe() {
        return inst.getUnsafe();
    }

    @Override
    @Nullable
    protected NamespacedKey findDummyRecipeKey(@Nonnull BaseCookingRecipe recipe) {
        boolean isCampfireRecipe = recipe instanceof CampfireRecipe;
        boolean isSmokingRecipe = recipe instanceof SmokingRecipe;
        boolean isBlastingRecipe = recipe instanceof BlastingRecipe;
        ItemStack originalItem = new ItemStack(recipe.getOriginal().getOriginalType());
        for (var iterator = Bukkit.recipeIterator(); iterator.hasNext(); ) {
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
                    logger.info("registered " + recipe.getKey() + " as campfire recipe!");
                } else if (recipe instanceof SmokingRecipe) {
                    logger.info("registered " + recipe.getKey() + " as smoker recipe!");
                } else if (recipe instanceof BlastingRecipe) {
                    logger.info("registered " + recipe.getKey() + " as blast-furnace recipe!");
                } else if (recipe instanceof FurnaceRecipe) {
                    logger.info("registered " + recipe.getKey() + " as furnace recipe!");
                } else {
                    logger.info("registered " + recipe.getKey() + " as unknown-type cooking recipe!");
                }
            }
        }
    }
}
