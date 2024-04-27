package org.ricetea.barleyteaapi.internal.item.registration;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.recipe.BaseCraftingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.ShapedCraftingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.ShapelessCraftingRecipe;
import org.ricetea.barleyteaapi.api.item.registration.CraftingRecipeRegister;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

@Singleton
@ApiStatus.Internal
public final class CraftingRecipeRegisterImpl extends BaseRecipeRegisterImpl<BaseCraftingRecipe> implements CraftingRecipeRegister {

    public CraftingRecipeRegisterImpl() {
        super("dummy_crafting_recipe");
    }

    @Override
    @Nullable
    protected NamespacedKey findDummyRecipeKey(@Nonnull BaseCraftingRecipe recipe) {
        if (recipe instanceof ShapedCraftingRecipe shapedRecipe) {
            return findShapedDummyRecipeKey(shapedRecipe);
        } else if (recipe instanceof ShapelessCraftingRecipe shapelessRecipe) {
            return findShapelessDummyRecipeKey(shapelessRecipe);
        } else {
            return null;
        }
    }

    @Nullable
    private NamespacedKey findShapedDummyRecipeKey(@Nonnull ShapedCraftingRecipe recipe) {
        int column = recipe.getColumnCount();
        int row = recipe.getRowCount();
        ItemStack[] ingredients = recipe.getIngredientMatrix().stream()
                .map(this::generateTestOnlyItemStack)
                .toArray(ItemStack[]::new);
        for (var iterator = Bukkit.recipeIterator(); iterator.hasNext(); ) {
            Recipe cRecipe = iterator.next();
            if (!(cRecipe instanceof ShapedRecipe craftRecipe))
                continue;
            String[] recipeShape = craftRecipe.getShape();
            {
                int length = recipeShape.length;
                if (length != row) {
                    continue;
                }
                for (int i = 0; i < length; i++) {
                    if (recipeShape[i].length() != column) {
                        length = -1;
                        break;
                    }
                }
                if (length <= 0)
                    continue;
            }
            Map<Character, RecipeChoice> recipeChoiceMap = craftRecipe.getChoiceMap();
            boolean flag = false;
            for (int r = 0; r < row; r++) {
                String shapeLine = recipeShape[r];
                for (int c = 0; c < column; c++) {
                    RecipeChoice choice = recipeChoiceMap.get(shapeLine.charAt(c));
                    ItemStack itemStack = ingredients[r * column + c];
                    if (itemStack == null) {
                        if (choice == null)
                            continue;
                        if (!choice.test(new ItemStack(Material.AIR))) {
                            flag = true;
                            break;
                        }
                    } else {
                        if (choice == null || !choice.test(itemStack)) {
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag)
                    break;
            }
            if (flag)
                continue;
            return craftRecipe.getKey();
        }
        return null;
    }

    @Nullable
    private NamespacedKey findShapelessDummyRecipeKey(@Nonnull ShapelessCraftingRecipe recipe) {
        List<ItemStack> ingredients = recipe.getIngredients().stream()
                .map(this::generateTestOnlyItemStack)
                .filter(Objects::nonNull)
                .toList();
        for (var iterator = Bukkit.recipeIterator(); iterator.hasNext(); ) {
            if (!(iterator.next() instanceof ShapelessRecipe craftRecipe))
                continue;

            List<RecipeChoice> craftIngredients = craftRecipe.getChoiceList();
            if (ingredients.size() != craftIngredients.size())
                continue;

            craftIngredients = new ArrayList<>(craftIngredients);
            ArrayList<ItemStack> modifiableIngredients = new ArrayList<>(ingredients);

            for (var iterator2 = modifiableIngredients.listIterator(); iterator2.hasNext(); ) {
                ItemStack itemStack = iterator2.next();
                for (var iterator3 = craftIngredients.listIterator(); iterator3.hasNext(); ) {
                    if (iterator3.next().test(itemStack)) {
                        iterator3.remove();
                        iterator2.remove();
                        break;
                    }
                }
            }

            if (craftIngredients.isEmpty() && modifiableIngredients.isEmpty())
                return craftRecipe.getKey();
        }
        return null;
    }

    @Nullable
    private ItemStack generateTestOnlyItemStack(@Nullable CustomItemType itemType) {
        if (itemType == null || itemType.isEmpty())
            return null;
        ItemStack result = null;
        CustomItem customItem = itemType.asCustomItem();
        if (customItem instanceof FeatureItemGive feature) {
            result = feature.handleItemGive(1);
        }
        if (result == null) {
            result = new ItemStack(itemType.getOriginalType());
            if (customItem != null) {
                ItemHelper.register(customItem, result);
            }
        }
        return result;
    }

    @Override
    protected void afterRegisterRecipe(@Nonnull BaseCraftingRecipe recipe) {
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                Logger logger = inst.getLogger();
                NamespacedKey key = recipe.getKey();
                if (recipe instanceof ShapedCraftingRecipe) {
                    logger.info(LOGGING_REGISTERED_FORMAT.formatted(key, "shaped crafting recipe"));
                } else if (recipe instanceof ShapelessCraftingRecipe) {
                    logger.info(LOGGING_REGISTERED_FORMAT.formatted(key, "shapeless crafting recipe"));
                } else {
                    logger.info(LOGGING_REGISTERED_FORMAT.formatted(key, "unknown-type crafting recipe"));
                }
            }
        }
    }
}
