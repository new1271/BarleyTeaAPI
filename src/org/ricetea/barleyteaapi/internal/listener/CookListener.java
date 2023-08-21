package org.ricetea.barleyteaapi.internal.listener;

import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.CampfireStartEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.recipe.BaseCookingRecipe;
import org.ricetea.barleyteaapi.api.item.registration.CookingRecipeRegister;
import org.ricetea.barleyteaapi.api.item.render.AbstractItemRenderer;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class CookListener implements Listener {
    private static final Lazy<CookListener> inst = new Lazy<>(CookListener::new);

    private CookListener() {
    }

    @Nonnull
    public static CookListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenBlockCook(BlockCookEvent event) {
        if (event == null)
            return;
        Recipe cookRecipe = event.getRecipe();
        if (cookRecipe instanceof Keyed keyedRecipe) {
            DataItemType itemType = ObjectUtil.letNonNull(
                    ObjectUtil.mapWhenNonnull(event.getSource(), BaseItem::getItemType),
                    DataItemType::empty);
            NamespacedKey recipeKey = keyedRecipe.getKey();
            Block block = event.getBlock();
            if (block != null && (!recipeKey.getNamespace().equals(NamespacedKey.MINECRAFT) || itemType.isRight())) {
                boolean allPassed = true;
                final ItemStack oldResult = event.getResult();
                ItemStack result = oldResult;
                CookingRecipeRegister register = CookingRecipeRegister.getInstanceUnsafe();
                if (register != null && register.hasAnyRegisteredRecipe()) {
                    List<BaseCookingRecipe> recipes = register.lookupRecipeFromDummies(recipeKey);
                    if (recipes != null) {
                        for (BaseCookingRecipe recipe : recipes) {
                            if (itemType.equals(recipe.getOriginal()) && recipe.filterAcceptedBlock(block)) {
                                result = recipe.apply(event.getSource());
                                allPassed = false;
                                break;
                            }
                        }
                    }
                }
                if (allPassed) {
                    result = null;
                }
                if (oldResult != result) {
                    if (BaseItem.isBarleyTeaItem(result)) {
                        AbstractItemRenderer.renderItem(result);
                    }
                    event.setResult(result != null ? result : event.getSource());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenFurnaceStartSmelt(FurnaceStartSmeltEvent event) {
        if (event == null)
            return;
        Block block = event.getBlock();
        if (block == null)
            return;
        Recipe furnaceRecipe = event.getRecipe();
        if (furnaceRecipe instanceof Keyed keyedRecipe) {
            DataItemType itemType = ObjectUtil.letNonNull(
                    ObjectUtil.mapWhenNonnull(event.getSource(), BaseItem::getItemType),
                    DataItemType::empty);
            NamespacedKey recipeKey = keyedRecipe.getKey();
            if (!recipeKey.getNamespace().equals(NamespacedKey.MINECRAFT) || itemType.isRight()) {
                CookingRecipeRegister register = CookingRecipeRegister.getInstanceUnsafe();
                if (register != null && register.hasAnyRegisteredRecipe()) {
                    List<BaseCookingRecipe> recipes = register.lookupRecipeFromDummies(recipeKey);
                    if (recipes != null) {
                        for (BaseCookingRecipe recipe : recipes) {
                            if (itemType.equals(recipe.getOriginal()) && recipe.filterAcceptedBlock(block)) {
                                event.setTotalCookTime(event.getSource().getAmount() * recipe.getCookingTime());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenCampfireStartCook(CampfireStartEvent event) {
        if (event == null)
            return;
        Block block = event.getBlock();
        if (block == null)
            return;
        CampfireRecipe campfireRecipe = event.getRecipe();
        DataItemType itemType = ObjectUtil.letNonNull(
                ObjectUtil.mapWhenNonnull(event.getSource(), BaseItem::getItemType),
                DataItemType::empty);
        NamespacedKey recipeKey = campfireRecipe.getKey();
        if (!recipeKey.getNamespace().equals(NamespacedKey.MINECRAFT) || itemType.isRight()) {
            CookingRecipeRegister register = CookingRecipeRegister.getInstanceUnsafe();
            if (register != null && register.hasAnyRegisteredRecipe()) {
                List<BaseCookingRecipe> recipes = register.lookupRecipeFromDummies(recipeKey);
                if (recipes != null) {
                    for (BaseCookingRecipe recipe : recipes) {
                        if (itemType.equals(recipe.getOriginal()) && recipe.filterAcceptedBlock(block)) {
                            event.setTotalCookTime(recipe.getCookingTime());
                            break;
                        }
                    }
                }
            }
        }
    }

}
