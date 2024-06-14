package org.ricetea.barleyteaapi.api.item.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.item.CustomItemType;

import javax.annotation.Nonnull;

public class SmokingRecipe extends BaseCookingRecipe {
    public SmokingRecipe(@Nonnull NamespacedKey key, @Nonnull CustomItemType original, @Nonnull CustomItemType result) {
        super(key, original, result);
    }

    public SmokingRecipe(@Nonnull NamespacedKey key, @Nonnull CustomItemType original, @Nonnull CustomItemType result,
                         float experience, int cookingTime) {
        super(key, original, result, experience, cookingTime);
    }

    @Nonnull
    public org.bukkit.inventory.SmokingRecipe toBukkitRecipe(@Nonnull NamespacedKey key) {
        return new org.bukkit.inventory.SmokingRecipe(key, new ItemStack(getResult().getOriginalType()),
                getOriginal().getOriginalType(), getExperience(), getCookingTime());
    }

    @Override
    public boolean filterAcceptedBlock(@Nonnull Block block) {
        return switch (block.getType()) {
            case SMOKER, FURNACE -> true;
            default -> false;
        };
    }
}
