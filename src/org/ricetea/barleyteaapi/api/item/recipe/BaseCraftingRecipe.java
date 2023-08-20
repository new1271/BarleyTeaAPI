package org.ricetea.barleyteaapi.api.item.recipe;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;

public abstract class BaseCraftingRecipe extends BaseRecipe implements Function<ItemStack[], ItemStack> {
    public BaseCraftingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType result) {
        super(key, result);
    }

    public abstract boolean checkMatrixOfTypes(@Nonnull DataItemType[] matrix);

    @Nullable
    public abstract ItemStack apply(ItemStack[] matrix);
}
