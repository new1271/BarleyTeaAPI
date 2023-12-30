package org.ricetea.barleyteaapi.internal.item.registration;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.api.item.recipe.BaseCraftingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.ShapedCraftingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.ShapelessCraftingRecipe;
import org.ricetea.barleyteaapi.api.item.registration.CraftingRecipeRegister;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Singleton
@ApiStatus.Internal
public final class CraftingRecipeRegisterImpl extends BaseRecipeRegisterImpl<BaseCraftingRecipe> implements CraftingRecipeRegister {

    @Nonnull
    private static final Lazy<CraftingRecipeRegisterImpl> inst = Lazy.create(CraftingRecipeRegisterImpl::new);

    private CraftingRecipeRegisterImpl() {
        super("dummy_crafting_recipe");
    }

    @Nonnull
    public static CraftingRecipeRegisterImpl getInstance() {
        return inst.get();
    }

    @Nullable
    public static CraftingRecipeRegisterImpl getInstanceUnsafe() {
        return inst.getUnsafe();
    }

    @Override
    @Nullable
    protected NamespacedKey findDummyRecipeKey(@Nonnull BaseCraftingRecipe recipe) {
        List<CustomItemType> ingredients;
        if (recipe instanceof ShapedCraftingRecipe shapedRecipe) {
            ingredients = shapedRecipe.getIngredientMatrix();
        } else if (recipe instanceof ShapelessCraftingRecipe shapelessRecipe) {
            ingredients = shapelessRecipe.getIngredients();
        } else {
            return null;
        }
        ItemStack[] testingItemStacks = ingredients.stream()
                .map(CustomItemType::getOriginalType)
                .map(ItemStack::new)
                .toArray(ItemStack[]::new);
        if (testingItemStacks.length < 9) {
            testingItemStacks = Arrays.copyOf(testingItemStacks, 9);
        }
        return ObjectUtil.safeMap(
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
                    logger.info("registered " + recipe.getKey() + " as shaped crafting recipe!");
                } else if (recipe instanceof ShapelessCraftingRecipe) {
                    logger.info("registered " + recipe.getKey() + " as shapeless crafting recipe!");
                } else {
                    logger.info("registered " + recipe.getKey() + " as unknown-type crafting recipe!");
                }
            }
        }
    }
}
