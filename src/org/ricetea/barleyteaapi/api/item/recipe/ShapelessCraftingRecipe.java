package org.ricetea.barleyteaapi.api.item.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public class ShapelessCraftingRecipe extends BaseCraftingRecipe {

    @Nonnull
    private final DataItemType[] ingredients;
    @Nonnull
    private final DataItemType result;

    public ShapelessCraftingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType[] ingredients,
            @Nonnull DataItemType result) throws Exception {
        super(key);
        int length = ingredients.length;
        if (length <= 0) {
            throw new Exception("'ingredientMatrix' can't be empty!");
        } else if (length > 9) {
            throw new Exception("'ingredientMatrix' too large!");
        }
        this.ingredients = ingredients;
        if (result.isRight() && !(result.right() instanceof FeatureItemGive)) {
            throw new Exception("'result' isn't implement FeatureItemGive, recipe can't constructed!");
        }
        this.result = result;
    }

    @Nonnull
    public List<DataItemType> getIngredients() {
        return ObjectUtil.letNonNull(Collections.unmodifiableList(Arrays.asList(getIngredients0())),
                Collections::emptyList);
    }

    @Nonnull
    protected DataItemType[] getIngredients0() {
        return ingredients;
    }

    @Nonnull
    public DataItemType getResult() {
        return result;
    }

    @Override
    public boolean checkMatrixOfTypes(@Nonnull DataItemType[] matrix) {
        DataItemType[] ingredients = getIngredients0();
        if (matrix.length <= 0 || matrix.length < ingredients.length)
            return false;
        ArrayList<DataItemType> matrixClone = new ArrayList<>(Arrays.asList(matrix));
        int selectedCount = 0;
        for (int i = 0, count = ingredients.length; i < count; i++) {
            DataItemType predictedIngredient = ObjectUtil.letNonNull(ingredients[i], DataItemType::empty);
            for (var iterator = matrixClone.iterator(); iterator.hasNext();) {
                DataItemType actualIngredient = ObjectUtil.letNonNull(iterator.next(), DataItemType::empty);
                if (predictedIngredient.equals(actualIngredient)) {
                    selectedCount++;
                    iterator.remove();
                    break;
                }
            }
        }
        return selectedCount >= ingredients.length && matrixClone.stream().filter(type -> !type.isEmpty()).count() <= 0;
    }

    @Override
    public ItemStack apply(ItemStack[] matrix) {
        return result.mapLeftOrRight(ItemStack::new, right -> {
            return ObjectUtil.mapWhenNonnull(ObjectUtil.tryCast(right, FeatureItemGive.class),
                    itemGiveFeature -> itemGiveFeature.handleItemGive(1));
        });
    }

    @Nonnull
    public static ShapelessRecipe toBukkitRecipe(ShapelessCraftingRecipe recipe, NamespacedKey key) {
        ShapelessRecipe result = new ShapelessRecipe(key,
                new ItemStack(recipe.result.mapLeftOrRight(m -> m, d -> d.getMaterialBasedOn())));
        for (DataItemType type : recipe.getIngredients()) {
            Material material = type.mapLeftOrRight(m -> m, d -> d.getMaterialBasedOn());
            result.addIngredient(material);
        }
        return ObjectUtil.throwWhenNull(result);
    }
}
