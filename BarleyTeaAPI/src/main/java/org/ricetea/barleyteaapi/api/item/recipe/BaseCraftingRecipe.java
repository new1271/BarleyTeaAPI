package org.ricetea.barleyteaapi.api.item.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.item.CustomItemType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public abstract class BaseCraftingRecipe extends BaseRecipe implements Function<ItemStack[], ItemStack> {
    public BaseCraftingRecipe(@Nonnull NamespacedKey key, @Nonnull CustomItemType result) {
        super(key, result);
    }

    public abstract boolean checkMatrixOfTypes(@Nonnull CustomItemType[] matrix);

    @Nullable
    public abstract ItemStack apply(@Nonnull ItemStack[] matrix);
}
