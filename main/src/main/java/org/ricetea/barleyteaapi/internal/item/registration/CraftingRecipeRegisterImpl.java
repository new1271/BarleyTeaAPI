package org.ricetea.barleyteaapi.internal.item.registration;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.helper.FeatureHelper;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.recipe.BaseCraftingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.ShapedCraftingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.ShapelessCraftingRecipe;
import org.ricetea.barleyteaapi.api.item.registration.CraftingRecipeRegister;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.*;
import java.util.logging.Logger;

@Singleton
@ApiStatus.Internal
public final class CraftingRecipeRegisterImpl extends BaseRecipeRegisterImpl<BaseCraftingRecipe> implements CraftingRecipeRegister {

    public CraftingRecipeRegisterImpl() {
        super("dummy_crafting_recipe");
    }

    @Override
    @Nullable
    protected Collection<NamespacedKey> findDummyRecipeKeys(@Nonnull BaseCraftingRecipe recipe) {
        if (recipe instanceof ShapedCraftingRecipe shapedRecipe) {
            return findShapedDummyRecipeKey(shapedRecipe);
        } else if (recipe instanceof ShapelessCraftingRecipe shapelessRecipe) {
            return findShapelessDummyRecipeKey(shapelessRecipe);
        } else {
            return null;
        }
    }

    @Nullable
    private Collection<NamespacedKey> findShapedDummyRecipeKey(@Nonnull ShapedCraftingRecipe recipe) {
        int column = recipe.getColumnCount();
        int row = recipe.getRowCount();
        if (column == 1 && row == 1) {
            List<CustomItemType> matrix = recipe.getIngredientMatrix();
            if (matrix.size() == 1) {
                return findSingleItemDummyRecipeKey(matrix.get(0));
            }
        }
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
            return Collections.singleton(craftRecipe.getKey());
        }
        return null;
    }

    @Nullable
    private Collection<NamespacedKey> findShapelessDummyRecipeKey(@Nonnull ShapelessCraftingRecipe recipe) {
        List<CustomItemType> ingredientTypes = recipe.getIngredients();
        if (ingredientTypes.size() == 1) {
            return findSingleItemDummyRecipeKey(ingredientTypes.get(0));
        }
        List<ItemStack> ingredientList = ingredientTypes.stream()
                .map(this::generateTestOnlyItemStack)
                .filter(Objects::nonNull)
                .toList();
        Lazy<List<NamespacedKey>> resultLazy = Lazy.create(() -> new ArrayList<>(2));
        for (var iterator = Bukkit.recipeIterator(); iterator.hasNext(); ) {
            Recipe bukkitRecipe = iterator.next();
            if (!(bukkitRecipe instanceof CraftingRecipe))
                continue;
            NamespacedKey iterationResult;
            if (bukkitRecipe instanceof ShapedRecipe shapedRecipe)
                iterationResult = findShapelessDummyRecipeKey(ingredientList, shapedRecipe);
            else if (bukkitRecipe instanceof ShapelessRecipe shapelessRecipe)
                iterationResult = findShapelessDummyRecipeKey(ingredientList, shapelessRecipe);
            else
                iterationResult = null;

            if (iterationResult == null)
                continue;

            resultLazy.get().add(iterationResult);
        }
        return resultLazy.getUnsafe();
    }

    @Nullable
    private NamespacedKey findShapelessDummyRecipeKey(@Nonnull List<ItemStack> ingredientList,
                                                      @Nonnull ShapedRecipe bukkitRecipe) {
        String[] shape = bukkitRecipe.getShape();
        Map<Character, RecipeChoice> choiceMap = bukkitRecipe.getChoiceMap();
        List<RecipeChoice> craftIngredients = new ArrayList<>(9);
        for (String shapeLine : shape) {
            for (int i = 0, length = shapeLine.length(); i < length; i++) {
                RecipeChoice choice = choiceMap.get(shapeLine.charAt(i));
                if (choice == null)
                    continue;
                if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
                    List<Material> choices = materialChoice.getChoices();
                    if (choices.size() == 1 && Boolean.TRUE.equals(ObjectUtil.safeMap(choices.get(0), Material::isAir))) {
                        continue;
                    }
                }
                craftIngredients.add(choice);
            }
        }
        if (ingredientList.size() != craftIngredients.size())
            return null;

        craftIngredients = new ArrayList<>(craftIngredients);
        ArrayList<ItemStack> modifiableIngredients = new ArrayList<>(ingredientList);

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
            return bukkitRecipe.getKey();
        return null;
    }

    @Nullable
    private NamespacedKey findShapelessDummyRecipeKey(@Nonnull List<ItemStack> ingredientList,
                                                      @Nonnull ShapelessRecipe bukkitRecipe) {
        List<RecipeChoice> craftIngredients = bukkitRecipe.getChoiceList();
        if (ingredientList.size() != craftIngredients.size())
            return null;
        craftIngredients = new ArrayList<>(craftIngredients);
        ArrayList<ItemStack> modifiableIngredients = new ArrayList<>(ingredientList);

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
            return bukkitRecipe.getKey();
        return null;
    }

    @Nullable
    private Collection<NamespacedKey> findSingleItemDummyRecipeKey(@Nonnull CustomItemType itemType) {
        for (var iterator = Bukkit.recipeIterator(); iterator.hasNext(); ) {
            Recipe recipe = iterator.next();
            RecipeChoice recipeChoice = null;
            if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                List<RecipeChoice> choiceList = shapelessRecipe.getChoiceList();
                if (choiceList.size() == 1)
                    recipeChoice = choiceList.get(0);
            } else if (recipe instanceof ShapedRecipe shapedRecipe) {
                String[] shapes = shapedRecipe.getShape();
                if (shapes != null && shapes.length == 1) {
                    String shape = shapes[0];
                    if (shape != null && shape.length() == 1) {
                        recipeChoice = shapedRecipe.getChoiceMap().get(shape.charAt(0));
                    }
                }
            }
            if (recipeChoice == null)
                continue;
            ItemStack testItemStack = generateTestOnlyItemStack(itemType);
            if (testItemStack == null || !recipeChoice.test(testItemStack))
                continue;
            NamespacedKey result = ObjectUtil.tryMap(ObjectUtil.tryCast(recipe, Keyed.class), Keyed::getKey);
            if (result == null)
                continue;
            return Collections.singleton(result);
        }
        return null;
    }

    @Nullable
    private ItemStack generateTestOnlyItemStack(@Nullable CustomItemType itemType) {
        if (itemType == null || itemType.isEmpty())
            return null;
        CustomItem customItem = itemType.asCustomItem();
        ItemStack result = FeatureHelper.mapIfHasFeature(
                customItem,
                FeatureItemGive.class,
                feature -> feature.handleItemGive(1)
        );
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
