package org.ricetea.barleyteaapi.api.item.recipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public class ShapedCraftingRecipe extends BaseCraftingRecipe {

    @Nonnull
    private final DataItemType[] ingredientMatrix;
    @Nonnull
    private final DataItemType result;
    private final int colCount, rowCount;

    public ShapedCraftingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType[] ingredientMatrix,
            int colCount, @Nonnull DataItemType result) throws Exception {
        super(key);
        if (colCount < 1 || colCount > 3) {
            throw new Exception("'colCount' can't lower than 1 or greater then 3!");
        }
        int length = ingredientMatrix.length;
        if (length <= 0) {
            throw new Exception("'ingredientMatrix' can't be empty!");
        } else {
            int remain = length % colCount;
            if (remain > 0) {
                throw new Exception("'ingredientMatrix' must be a matrix!");
            } else {
                int rowCount = length / colCount;
                if (rowCount > 3) {
                    throw new Exception("'ingredientMatrix' is an invalid matrix!");
                }
                this.colCount = colCount;
                this.rowCount = rowCount;
            }
        }
        this.ingredientMatrix = ingredientMatrix;
        if (result.isRight() && !(result.right() instanceof FeatureItemGive)) {
            throw new Exception("'result' isn't implement FeatureItemGive, recipe can't constructed!");
        }
        this.result = result;
    }

    @Nonnull
    public List<DataItemType> getIngredientMatrix() {
        return ObjectUtil.letNonNull(Collections.unmodifiableList(Arrays.asList(getIngredientMatrix0())),
                Collections::emptyList);
    }

    @Nonnull
    protected DataItemType[] getIngredientMatrix0() {
        return ingredientMatrix;
    }

    @Nonnull
    public DataItemType getResult() {
        return result;
    }

    public int getColumnCount() {
        return colCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    @Override
    public boolean checkMatrixOfTypes(@Nonnull DataItemType[] matrix) {
        DataItemType[] ingredientMatrix = getIngredientMatrix0();
        int length = matrix.length;
        int lengthForSide;
        if (length == 9) {
            lengthForSide = 3;
        } else if (length == 4) {
            lengthForSide = 2;
        } else {
            double sqrt = Math.sqrt(length);
            if (sqrt % 1.0 == 1.0) {
                lengthForSide = (int) sqrt;
            } else {
                lengthForSide = 0;
            }
        }
        int colCount = getColumnCount();
        int rowCount = getRowCount();
        if (lengthForSide > 0 && lengthForSide >= colCount && lengthForSide >= rowCount) {
            for (int i = lengthForSide; i >= colCount; i--) {
                for (int j = lengthForSide; j >= rowCount; j--) {
                    boolean found = true;
                    for (int col = 0; col < colCount; col++) {
                        for (int row = 0; row < rowCount; row++) {
                            int colIndex = lengthForSide - i + col;
                            int rowIndex = lengthForSide - j + row;
                            DataItemType predictedIngredient = ObjectUtil.letNonNull(
                                    ingredientMatrix[row * colCount + col],
                                    DataItemType::empty);
                            DataItemType actualIngredient = ObjectUtil.letNonNull(
                                    matrix[rowIndex * lengthForSide + colIndex],
                                    DataItemType::empty);
                            if (!predictedIngredient.equals(actualIngredient)) {
                                found = false;
                                break;
                            }
                        }
                        if (!found) {
                            break;
                        }
                    }
                    if (found)
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack apply(ItemStack[] matrix) {
        return result.mapLeftOrRight(ItemStack::new, right -> {
            return ObjectUtil.mapWhenNonnull(ObjectUtil.tryCast(right, FeatureItemGive.class),
                    itemGiveFeature -> itemGiveFeature.handleItemGive(1));
        });
    }

    @Nonnull
    public static ShapedRecipe toBukkitRecipe(ShapedCraftingRecipe recipe, NamespacedKey key) {
        ShapedRecipe result = new ShapedRecipe(key,
                new ItemStack(recipe.result.mapLeftOrRight(m -> m, d -> d.getMaterialBasedOn())));
        HashMap<Material, Character> collectMap = new HashMap<>();
        char c = 'a';
        int colCount = recipe.getColumnCount();
        String[] shape = new String[recipe.getRowCount()];
        int currentIndex = 0;
        for (DataItemType type : recipe.getIngredientMatrix()) {
            Material material = type.mapLeftOrRight(m -> m, d -> d.getMaterialBasedOn());
            Character ct = collectMap.get(material);
            if (ct == null) {
                collectMap.put(material, ct = c++);
            }
            int currentRowIndex = currentIndex / colCount;
            String shapeLet = shape[currentRowIndex];
            if (shapeLet == null) {
                shape[currentRowIndex] = Character.toString(ct);
            } else {
                shape[currentRowIndex] = shapeLet + Character.toString(ct);
            }
            currentIndex++;
        }

        result = result.shape(shape);
        for (Map.Entry<Material, Character> entry : collectMap.entrySet()) {
            result = result.setIngredient(entry.getValue(), entry.getKey());
        }
        return ObjectUtil.throwWhenNull(result);
    }

}