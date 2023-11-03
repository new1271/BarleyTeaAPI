package org.ricetea.barleyteaapi.api.item.recipe;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;

public class SmokingRecipe extends BaseCookingRecipe {
    public SmokingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType original, @Nonnull DataItemType result) {
        super(key, original, result);
    }

    public SmokingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType original, @Nonnull DataItemType result,
            float experience, int cookingTime) {
        super(key, original, result, experience, cookingTime);
    }

    public org.bukkit.inventory.SmokingRecipe toBukkitRecipe(@Nonnull NamespacedKey key) {
        return new org.bukkit.inventory.SmokingRecipe(key, new ItemStack(getResult().getMaterialBasedOn()),
                getOriginal().getMaterialBasedOn(), getExperience(), getCookingTime());
    }

    @Override
    public boolean filterAcceptedBlock(@Nonnull Block block) {
        return block.getType().equals(Material.SMOKER);
    }
}
