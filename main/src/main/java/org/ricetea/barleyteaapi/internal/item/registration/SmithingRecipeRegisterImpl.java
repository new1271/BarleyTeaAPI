package org.ricetea.barleyteaapi.internal.item.registration;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.api.item.recipe.ArmorTrimSmithingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.BaseSmithingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.SmithingRecipe;
import org.ricetea.barleyteaapi.api.item.registration.SmithingRecipeRegister;
import org.ricetea.utils.CollectionUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

@Singleton
@ApiStatus.Internal
public final class SmithingRecipeRegisterImpl extends BaseRecipeRegisterImpl<BaseSmithingRecipe> implements SmithingRecipeRegister {

    public SmithingRecipeRegisterImpl() {
        super("dummy_smithing_recipe");
    }

    @Override
    @Nullable
    protected Collection<NamespacedKey> findDummyRecipeKeys(@Nonnull BaseSmithingRecipe recipe) {
        ItemStack originalItem = new ItemStack(recipe.getOriginal().getOriginalType());
        ItemStack templateItem = new ItemStack(
                CollectionUtil.firstOrDefault(recipe.getTemplates(), CustomItemType.empty())
                        .getOriginalType());
        ItemStack additionItem = new ItemStack(
                CollectionUtil.firstOrDefault(recipe.getAdditions(), CustomItemType.empty())
                        .getOriginalType());
        for (var iterator = Bukkit.recipeIterator(); iterator.hasNext(); ) {
            Recipe iteratingRecipe = iterator.next();
            if (iteratingRecipe instanceof SmithingTransformRecipe iteratingSmithingRecipe) {
                if (iteratingSmithingRecipe.getBase().test(originalItem)
                        && iteratingSmithingRecipe.getTemplate().test(templateItem)
                        && iteratingSmithingRecipe.getAddition().test(additionItem)) {
                    return Collections.singleton(iteratingSmithingRecipe.getKey());
                }
            } else if (iteratingRecipe instanceof SmithingTrimRecipe iteratingSmithingRecipe) {
                if (iteratingSmithingRecipe.getBase().test(originalItem)
                        && iteratingSmithingRecipe.getTemplate().test(templateItem)
                        && iteratingSmithingRecipe.getAddition().test(additionItem)) {
                    return Collections.singleton(iteratingSmithingRecipe.getKey());
                }
            }
        }
        return null;
    }

    @Override
    protected void afterRegisterRecipe(@Nonnull BaseSmithingRecipe recipe) {
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                Logger logger = inst.getLogger();
                NamespacedKey key = recipe.getKey();
                if (recipe instanceof SmithingRecipe) {
                    logger.info(LOGGING_REGISTERED_FORMAT.formatted(key, "normal smithing recipe"));
                } else if (recipe instanceof ArmorTrimSmithingRecipe) {
                    logger.info(LOGGING_REGISTERED_FORMAT.formatted(key, "armor-trimming smithing recipe"));
                } else {
                    logger.info(LOGGING_REGISTERED_FORMAT.formatted(key, "unknown-type smithing recipe"));
                }
            }
        }
    }
}
