package org.ricetea.barleyteaapi.internal.item.registration;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.recipe.*;
import org.ricetea.barleyteaapi.api.item.registration.CookingRecipeRegister;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

@Singleton
@ApiStatus.Internal
public final class CookingRecipeRegisterImpl extends BaseRecipeRegisterImpl<BaseCookingRecipe> implements CookingRecipeRegister {

    public CookingRecipeRegisterImpl() {
        super("dummy_cooking_recipe");
    }

    @Override
    @Nullable
    protected Collection<NamespacedKey> findDummyRecipeKeys(@Nonnull BaseCookingRecipe recipe) {
        if (recipe instanceof CampfireRecipe castedRecipe)
            return findDummyRecipeKeys(castedRecipe);
        if (recipe instanceof FurnaceRecipe castedRecipe)
            return findDummyRecipeKeys(castedRecipe);
        if (recipe instanceof SmokingRecipe castedRecipe)
            return findDummyRecipeKeys(castedRecipe);
        if (recipe instanceof BlastingRecipe castedRecipe)
            return findDummyRecipeKeys(castedRecipe);
        return null;
    }

    @Nullable
    private Collection<NamespacedKey> findDummyRecipeKeys(@Nonnull CampfireRecipe recipe) {
        return findDummyRecipeKeys(recipe, org.bukkit.inventory.CampfireRecipe.class);
    }

    @Nullable
    private Collection<NamespacedKey> findDummyRecipeKeys(@Nonnull SmokingRecipe recipe) {
        return findDummyRecipeKeys(recipe,
                org.bukkit.inventory.SmokingRecipe.class,
                org.bukkit.inventory.BlastingRecipe.class);
    }

    @Nullable
    private Collection<NamespacedKey> findDummyRecipeKeys(@Nonnull BlastingRecipe recipe) {
        return findDummyRecipeKeys(recipe,
                org.bukkit.inventory.FurnaceRecipe.class,
                org.bukkit.inventory.BlastingRecipe.class);
    }

    @Nullable
    private Collection<NamespacedKey> findDummyRecipeKeys(@Nonnull FurnaceRecipe recipe) {
        return findDummyRecipeKeys(recipe,
                org.bukkit.inventory.FurnaceRecipe.class,
                org.bukkit.inventory.SmokingRecipe.class,
                org.bukkit.inventory.BlastingRecipe.class);
    }

    @SafeVarargs
    @Nullable
    private Collection<NamespacedKey> findDummyRecipeKeys(@Nonnull BaseCookingRecipe recipe,
                                                          @Nullable Class<? extends org.bukkit.inventory.CookingRecipe<?>>... classes) {
        if (classes == null)
            return null;
        Material originalType = recipe.getOriginal().getOriginalType();
        Lazy<ItemStack> testingItemStackLazy = Lazy.create(() -> new ItemStack(originalType));
        Lazy<Set<NamespacedKey>> resultLazy = Lazy.create(HashSet::new);
        for (Iterator<Recipe> iterator = Bukkit.recipeIterator(); iterator.hasNext(); ) {
            Recipe rawBukkitRecipe = iterator.next();
            org.bukkit.inventory.CookingRecipe<?> bukkitRecipe = null;
            for (Class<? extends org.bukkit.inventory.CookingRecipe<?>> clazz : classes) {
                bukkitRecipe = ObjectUtil.tryCast(rawBukkitRecipe, clazz);
                if (bukkitRecipe != null)
                    break;
            }
            if (bukkitRecipe == null)
                continue;
            if (bukkitRecipe.getInputChoice().test(testingItemStackLazy.get())) {
                resultLazy.get().add(bukkitRecipe.getKey());
            }
        }
        return resultLazy.getUnsafe();
    }

    @Override
    protected void afterRegisterRecipe(@Nonnull BaseCookingRecipe recipe) {
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                Logger logger = inst.getLogger();
                NamespacedKey key = recipe.getKey();
                if (recipe instanceof CampfireRecipe) {
                    logger.info(LOGGING_REGISTERED_FORMAT.formatted(key, "campfire recipe"));
                } else if (recipe instanceof SmokingRecipe) {
                    logger.info(LOGGING_REGISTERED_FORMAT.formatted(key, "smoker recipe"));
                } else if (recipe instanceof BlastingRecipe) {
                    logger.info(LOGGING_REGISTERED_FORMAT.formatted(key, "blast-furnace recipe"));
                } else if (recipe instanceof FurnaceRecipe) {
                    logger.info(LOGGING_REGISTERED_FORMAT.formatted(key, "furnace recipe"));
                } else {
                    logger.info(LOGGING_REGISTERED_FORMAT.formatted(key, "unknown-type cooking recipe"));
                }
            }
        }
    }
}
