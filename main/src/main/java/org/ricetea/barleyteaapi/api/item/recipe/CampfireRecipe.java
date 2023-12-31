package org.ricetea.barleyteaapi.api.item.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.item.CustomItemType;

import javax.annotation.Nonnull;

public class CampfireRecipe extends BaseCookingRecipe {
    public CampfireRecipe(@Nonnull NamespacedKey key, @Nonnull CustomItemType original, @Nonnull CustomItemType result) {
        super(key, original, result);
    }

    public CampfireRecipe(@Nonnull NamespacedKey key, @Nonnull CustomItemType original, @Nonnull CustomItemType result,
                          float experience, int cookingTime) {
        super(key, original, result, experience, cookingTime);
    }

    @Nonnull
    public org.bukkit.inventory.CampfireRecipe toBukkitRecipe(@Nonnull NamespacedKey key) {
        return new org.bukkit.inventory.CampfireRecipe(key, new ItemStack(getResult().getOriginalType()),
                getOriginal().getOriginalType(), getExperience(), getCookingTime());
    }

    @Override
    public boolean filterAcceptedBlock(@Nonnull Block block) {
        return block.getType().equals(Material.CAMPFIRE);
    }

}
