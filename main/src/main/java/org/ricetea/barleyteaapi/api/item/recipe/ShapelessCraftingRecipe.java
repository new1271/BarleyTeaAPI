package org.ricetea.barleyteaapi.api.item.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import java.util.*;

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
        this.ingredients = ingredients;
    }

    @Nonnull
    public List<CustomItemType> getIngredients() {
        return ObjectUtil.letNonNull(Collections.unmodifiableList(Arrays.asList(getIngredients0())),
                Collections::emptyList);
    }

    @Nonnull
    protected CustomItemType[] getIngredients0() {
        return ingredients;
    }

    @Override
    public boolean checkMatrixOfTypes(@Nonnull CustomItemType[] matrix) {
        CustomItemType[] ingredients = getIngredients0();
        if (matrix.length == 0 || matrix.length < ingredients.length)
            return false;
        ArrayList<CustomItemType> matrixClone = new ArrayList<>(Arrays.asList(matrix));
        int selectedCount = 0;
        for (CustomItemType ingredient : ingredients) {
            CustomItemType predictedIngredient = ObjectUtil.letNonNull(ingredient, CustomItemType::empty);
            for (var iterator = matrixClone.iterator(); iterator.hasNext(); ) {
                CustomItemType actualIngredient = ObjectUtil.letNonNull(iterator.next(), CustomItemType::empty);
                if (predictedIngredient.equals(actualIngredient)) {
                    selectedCount++;
                    iterator.remove();
                    break;
                }
            }
        }
        return selectedCount >= ingredients.length && matrixClone.stream().allMatch(CustomItemType::isEmpty);
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
                new ItemStack(getResult().getOriginalType()));
        for (CustomItemType type : getIngredients()) {
            Material material = type.getOriginalType();
            result.addIngredient(material);
        }
        return Objects.requireNonNull(result);
    }
}
