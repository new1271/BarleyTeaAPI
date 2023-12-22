package org.ricetea.barleyteaapi.api.item.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;

import javax.annotation.Nonnull;

public class BlastingRecipe extends BaseCookingRecipe {
    public BlastingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType original, @Nonnull DataItemType result) {
        super(key, original, result);
    }

    public BlastingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType original, @Nonnull DataItemType result,
            float experience, int cookingTime) {
        super(key, original, result, experience, cookingTime);
    }

    @Nonnull
    public org.bukkit.inventory.BlastingRecipe toBukkitRecipe(@Nonnull NamespacedKey key) {
        return new org.bukkit.inventory.BlastingRecipe(key, new ItemStack(getResult().getMaterialBasedOn()),
                getOriginal().getMaterialBasedOn(), getExperience(), getCookingTime());
    }

    @Override
    public boolean filterAcceptedBlock(@Nonnull Block block) {
        return block.getType().equals(Material.BLAST_FURNACE);
    }
}
