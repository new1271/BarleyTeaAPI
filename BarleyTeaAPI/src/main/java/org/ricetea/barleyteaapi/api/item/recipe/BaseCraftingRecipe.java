package org.ricetea.barleyteaapi.api.item.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.utils.function.NonnullFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BaseCraftingRecipe extends BaseRecipe implements NonnullFunction<ItemStack[], ItemStack> {
    public BaseCraftingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType result) {
        super(key, result);
    }

    public abstract boolean checkMatrixOfTypes(@Nonnull DataItemType[] matrix);

    @Nullable
    public abstract ItemStack apply(@Nonnull ItemStack[] matrix);
}
