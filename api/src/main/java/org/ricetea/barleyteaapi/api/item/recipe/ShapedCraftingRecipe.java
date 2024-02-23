package org.ricetea.barleyteaapi.api.item.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.utils.Box;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Stream;

public class ShapedCraftingRecipe extends BaseCraftingRecipe {

    @Nonnull
    private final CustomItemType[] ingredientMatrix;
    private final int colCount, rowCount;

    public ShapedCraftingRecipe(@Nonnull NamespacedKey key, @Nonnull CustomItemType[] ingredientMatrix,
                                int colCount, @Nonnull CustomItemType result) {
        super(key, result);
        if (colCount < 1 || colCount > 3) {
            throw new UnsupportedOperationException("'colCount' can't lower than 1 or greater then 3!");
        }
        int length = ingredientMatrix.length;
        if (length == 0) {
            throw new UnsupportedOperationException("'ingredientMatrix' can't be empty!");
        } else {
            int remain = length % colCount;
            if (remain > 0) {
                throw new UnsupportedOperationException("'ingredientMatrix' must be a matrix!");
            } else {
                int rowCount = length / colCount;
                if (rowCount > 3) {
                    throw new UnsupportedOperationException("'ingredientMatrix' is an invalid matrix!");
                }
                this.colCount = colCount;
                this.rowCount = rowCount;
            }
        }
        if (Arrays.stream(ingredientMatrix).anyMatch(Objects::isNull)) {
            this.ingredientMatrix = Arrays.stream(ingredientMatrix)
                    .map(type -> ObjectUtil.letNonNull(type, CustomItemType::empty))
                    .toArray(CustomItemType[]::new);
        } else {
            this.ingredientMatrix = ingredientMatrix;
        }
    }

    public ShapedCraftingRecipe(@Nonnull NamespacedKey key, @Nonnull CustomItemType[][] ingredientMatrix,
                                @Nonnull CustomItemType result) {
        super(key, result);
        int length = ingredientMatrix.length;
        if (length == 0) {
            throw new UnsupportedOperationException("'ingredientMatrix' can't be empty!");
        } else {
            CustomItemType[] matrixTranslated;
            int colCount = 0;
            int rowCount = Math.min(3, length);
            for (int i = 0; i < rowCount; i++) {
                colCount = Math.max(colCount, ingredientMatrix[i].length);
            }
            final int finalColCount = Math.min(3, colCount);
            matrixTranslated = Arrays.stream(ingredientMatrix)
                    .limit(3)
                    .map(innerArray -> {
                        if (innerArray.length < finalColCount) {
                            return Arrays.copyOf(innerArray, finalColCount);
                        } else {
                            return innerArray;
                        }
                    })
                    .flatMap(Stream::of)
                    .map(type -> ObjectUtil.letNonNull(type, CustomItemType::empty))
                    .toArray(CustomItemType[]::new);
            this.ingredientMatrix = matrixTranslated;
            this.colCount = colCount;
            this.rowCount = rowCount;
        }
    }

    @Nonnull
    public List<CustomItemType> getIngredientMatrix() {
        return ObjectUtil.letNonNull(List.of(getIngredientMatrix0()), Collections::emptyList);
    }

    @Nonnull
    protected CustomItemType[] getIngredientMatrix0() {
        return ingredientMatrix;
    }

    public int getColumnCount() {
        return colCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    @Override
    public boolean checkMatrixOfTypes(@Nonnull CustomItemType[] matrix) {
        CustomItemType[] ingredientMatrix = getIngredientMatrix0();
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
                            CustomItemType predictedIngredient = ObjectUtil.letNonNull(
                                    ingredientMatrix[row * colCount + col],
                                    CustomItemType::empty);
                            CustomItemType actualIngredient = ObjectUtil.letNonNull(
                                    matrix[rowIndex * lengthForSide + colIndex],
                                    CustomItemType::empty);
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
    public ItemStack apply(@Nonnull ItemStack[] matrix) {
        return getResult().map(ItemStack::new, right ->
                ObjectUtil.safeMap(ObjectUtil.tryCast(right, FeatureItemGive.class),
                        itemGiveFeature -> itemGiveFeature.handleItemGive(1))
        );
    }

    @Nonnull
    public ShapedRecipe toBukkitRecipe(@Nonnull NamespacedKey key) {
        ShapedRecipe result = new ShapedRecipe(key,
                new ItemStack(getResult().getOriginalType()));
        EnumMap<Material, Character> collectMap = new EnumMap<>(Material.class);
        Box<Character> characterBox = Box.box('a');
        int colCount = getColumnCount();
        String[] shape = new String[getRowCount()];
        int currentIndex = 0;
        for (CustomItemType type : getIngredientMatrix()) {
            Material material = type.getOriginalType();
            char c = collectMap.computeIfAbsent(material,
                    (ignored) -> Objects.requireNonNull(characterBox.safeOperate(_c -> ++_c)));
            int currentRowIndex = currentIndex / colCount;
            String shapeLet = shape[currentRowIndex];
            if (shapeLet == null) {
                shape[currentRowIndex] = Character.toString(c);
            } else {
                shape[currentRowIndex] = shapeLet + c;
            }
            currentIndex++;
        }

        result = result.shape(shape);
        for (Map.Entry<Material, Character> entry : collectMap.entrySet()) {
            result = result.setIngredient(entry.getValue(), entry.getKey());
        }
        return Objects.requireNonNull(result);
    }

}
