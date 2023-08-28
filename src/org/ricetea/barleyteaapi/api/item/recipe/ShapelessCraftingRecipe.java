package org.ricetea.barleyteaapi.api.item.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    public ShapelessCraftingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType[] ingredients,
            @Nonnull DataItemType result) throws UnsupportedOperationException {
        super(key, result);
        int length = ingredients.length;
        if (length <= 0) {
            throw new UnsupportedOperationException("'ingredientMatrix' can't be empty!");
        } else if (length > 9) {
            throw new UnsupportedOperationException("'ingredientMatrix' too large!");
        }
        this.ingredients = ingredients;
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
        return getResult().mapLeftOrRight(ItemStack::new, right -> {
            return ObjectUtil.mapWhenNonnull(ObjectUtil.tryCast(right, FeatureItemGive.class),
                    itemGiveFeature -> itemGiveFeature.handleItemGive(1));
        });
    }

    @Nonnull
    public ShapelessRecipe toBukkitRecipe(NamespacedKey key) {
        ShapelessRecipe result = new ShapelessRecipe(key,
                new ItemStack(getResult().toMaterial()));
        for (DataItemType type : getIngredients()) {
            Material material = type.toMaterial();
            result.addIngredient(material);
        }
        return Objects.requireNonNull(result);
    }
}
