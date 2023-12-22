package org.ricetea.barleyteaapi.api.item.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import java.util.*;

public class ShapelessCraftingRecipe extends BaseCraftingRecipe {

    @Nonnull
    private final DataItemType[] ingredients;

    public ShapelessCraftingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType[] ingredients,
            @Nonnull DataItemType result) {
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
        if (matrix.length == 0 || matrix.length < ingredients.length)
            return false;
        ArrayList<DataItemType> matrixClone = new ArrayList<>(Arrays.asList(matrix));
        int selectedCount = 0;
        for (DataItemType ingredient : ingredients) {
            DataItemType predictedIngredient = ObjectUtil.letNonNull(ingredient, DataItemType::empty);
            for (var iterator = matrixClone.iterator(); iterator.hasNext(); ) {
                DataItemType actualIngredient = ObjectUtil.letNonNull(iterator.next(), DataItemType::empty);
                if (predictedIngredient.equals(actualIngredient)) {
                    selectedCount++;
                    iterator.remove();
                    break;
                }
            }
        }
        return selectedCount >= ingredients.length && matrixClone.stream().allMatch(DataItemType::isEmpty);
    }

    @Override
    public ItemStack apply(@Nonnull ItemStack[] matrix) {
        return getResult().map(ItemStack::new, right ->
            ObjectUtil.safeMap(ObjectUtil.tryCast(right, FeatureItemGive.class),
                    itemGiveFeature -> itemGiveFeature.handleItemGive(1))
        );
    }

    @Nonnull
    public ShapelessRecipe toBukkitRecipe(@Nonnull NamespacedKey key) {
        ShapelessRecipe result = new ShapelessRecipe(key,
                new ItemStack(getResult().getMaterialBasedOn()));
        for (DataItemType type : getIngredients()) {
            Material material = type.getMaterialBasedOn();
            result.addIngredient(material);
        }
        return Objects.requireNonNull(result);
    }
}
