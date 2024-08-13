package org.ricetea.barleyteaapi.api.item.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.ricetea.barleyteaapi.api.helper.FeatureHelper;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ShapelessCraftingRecipe extends BaseCraftingRecipe {

    @Nonnull
    private final CustomItemType[] ingredients;

    public ShapelessCraftingRecipe(@Nonnull NamespacedKey key, @Nonnull CustomItemType[] ingredients,
                                   @Nonnull CustomItemType result) {
        super(key, result);
        int length = ingredients.length;
        if (length <= 0) {
            throw new UnsupportedOperationException("'ingredientMatrix' can't be empty!");
        } else if (length > 9) {
            throw new UnsupportedOperationException("'ingredientMatrix' too large!");
        }
        this.ingredients = Arrays.stream(ingredients)
                .filter(Objects::nonNull)
                .filter(Predicate.not(CustomItemType::isEmpty))
                .toArray(CustomItemType[]::new);
    }

    @Nonnull
    public List<CustomItemType> getIngredients() {
        return Arrays.stream(getIngredients0()).toList();
    }

    @Nonnull
    protected CustomItemType[] getIngredients0() {
        return ingredients;
    }

    @Override
    public boolean checkMatrixOfTypes(@Nonnull CustomItemType[] matrix) {
        List<CustomItemType> ingredientList = getIngredients();
        ArrayList<CustomItemType> matrixList = new ArrayList<>(
                Arrays.stream(matrix)
                        .filter(Objects::nonNull)
                        .filter(Predicate.not(CustomItemType::isEmpty))
                        .toList());
        if (matrixList.size() == ingredientList.size()) {
            ingredientList = new ArrayList<>(ingredientList);
            for (var iterator = ingredientList.iterator(); iterator.hasNext(); ) {
                CustomItemType ingredient = iterator.next();
                for (var iterator2 = matrixList.iterator(); iterator2.hasNext(); ) {
                    CustomItemType matrixType = iterator2.next();
                    if (Objects.equals(ingredient, matrixType)) {
                        iterator.remove();
                        iterator2.remove();
                        break;
                    }
                }
            }
            return ingredientList.isEmpty() && matrixList.isEmpty();
        }
        return false;
    }

    @Override
    public ItemStack apply(@Nonnull ItemStack[] matrix) {
        return getResult().map(ItemStack::new, right ->
                FeatureHelper.mapIfHasFeature(
                        right,
                        FeatureItemGive.class,
                        feature -> feature.handleItemGive(1)
                )
        );
    }

    @Nonnull
    public ShapelessRecipe toBukkitRecipe(@Nonnull NamespacedKey key) {
        ShapelessRecipe result = new ShapelessRecipe(key,
                new ItemStack(getResult().getOriginalType()));
        for (CustomItemType type : getIngredients()) {
            Material material = type.getOriginalType();
            if (material.isAir())
                continue;
            result.addIngredient(material);
        }
        return Objects.requireNonNull(result);
    }
}
