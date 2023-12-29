package org.ricetea.barleyteaapi.api.item.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGive;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import java.util.function.Function;

public abstract class BaseCookingRecipe extends BaseRecipe implements Function<ItemStack, ItemStack> {

    public static final int DefaultCookingTime = 200;
    public static final float DefaultExperience = 0.0f;

    @Nonnull
    private final CustomItemType original;

    private final float experience;
    private final int cookingTime;

    public BaseCookingRecipe(@Nonnull NamespacedKey key, @Nonnull CustomItemType original, @Nonnull CustomItemType result)
            throws UnsupportedOperationException {
        this(key, original, result, 0.0f, DefaultCookingTime);
    }

    public BaseCookingRecipe(@Nonnull NamespacedKey key, @Nonnull CustomItemType original, @Nonnull CustomItemType result,
                             float experience, int cookingTime) {
        super(key, result);
        this.original = original;
        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    @Nonnull
    public CustomItemType getOriginal() {
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
        return ObjectUtil.letNonNull(getResult().map(ItemStack::new, right ->
                ObjectUtil.safeMap(ObjectUtil.tryCast(right, FeatureItemGive.class),
                        itemGiveFeature -> itemGiveFeature.handleItemGive(1))
        ), () -> new ItemStack(Material.AIR));
    }

}
