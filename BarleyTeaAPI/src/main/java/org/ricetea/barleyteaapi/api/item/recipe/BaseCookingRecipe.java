package org.ricetea.barleyteaapi.api.item.recipe;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.utils.ObjectUtil;
import org.ricetea.utils.function.NonnullFunction;

public abstract class BaseCookingRecipe extends BaseRecipe implements NonnullFunction<ItemStack, ItemStack> {

    public static final int DefaultCookingTime = 200;
    public static final float DefaultExperience = 0.0f;

    @Nonnull
    private final DataItemType original;

    private final float experience;
    private final int cookingTime;

    public BaseCookingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType original, @Nonnull DataItemType result)
            throws UnsupportedOperationException {
        this(key, original, result, 0.0f, DefaultCookingTime);
    }

    public BaseCookingRecipe(@Nonnull NamespacedKey key, @Nonnull DataItemType original, @Nonnull DataItemType result,
            float experience, int cookingTime) {
        super(key, result);
        this.original = original;
        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    @Nonnull
    public DataItemType getOriginal() {
        return original;
    }

    public float getExperience() {
        return experience;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public abstract boolean filterAcceptedBlock(@Nonnull Block block);

    @Nonnull
    public ItemStack apply(@Nonnull ItemStack source) {
        return ObjectUtil.letNonNull(getResult().map(ItemStack::new, right -> {
            return ObjectUtil.safeMap(ObjectUtil.tryCast(right, FeatureItemGive.class),
                    itemGiveFeature -> itemGiveFeature.handleItemGive(1));
        }), () -> new ItemStack(Material.AIR));
    }

}
