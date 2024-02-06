package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.CampfireStartEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.recipe.BaseCookingRecipe;
import org.ricetea.barleyteaapi.api.item.registration.CookingRecipeRegister;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class CookListener implements Listener {
    private static final Lazy<CookListener> inst = Lazy.create(CookListener::new);

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
        ItemStack source = ItemHelper.getSingletonClone(event.getSource());
        Recipe cookRecipe = event.getRecipe();
        if (cookRecipe instanceof Keyed keyedRecipe) {
            CustomItemType itemType = CustomItemType.get(source);
            NamespacedKey recipeKey = keyedRecipe.getKey();
            Block block = event.getBlock();
            if (!recipeKey.getNamespace().equals(NamespacedKey.MINECRAFT) || itemType.isCustomItem()) {
                boolean allPassed = true;
                final ItemStack oldResult = event.getResult();
                ItemStack result = oldResult;
                CookingRecipeRegister register = CookingRecipeRegister.getInstanceUnsafe();
                if (register != null && !register.isEmpty()) {
                    for (BaseCookingRecipe recipe : register.listAllAssociatedWithDummyRecipe(recipeKey)) {
                        if (itemType.equals(recipe.getOriginal()) && recipe.filterAcceptedBlock(block)) {
                            result = recipe.apply(source);
                            allPassed = false;
                            break;
                        }
                    }
                }
                if (allPassed) {
                    result = null;
                }
                if (oldResult != result) {
                    event.setResult(result != null ? result : event.getSource());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void listenFurnaceBurn(FurnaceBurnEvent event) {
        if (event == null)
            return;
        CookingRecipeRegister register = CookingRecipeRegister.getInstanceUnsafe();
        if (event.getBlock().getState() instanceof Furnace furnace && register != null) {
            ItemStack item = furnace.getInventory().getSmelting();
            CustomItem itemType = CustomItem.get(item);
            if (itemType != null) {
                if (register.findFirst(recipe -> itemType
                        .equals(recipe.getOriginal().asCustomItem())) == null) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(priority = EventPriority.LOW)
    public void listenFurnaceStartSmelt(FurnaceStartSmeltEvent event) {
        if (event == null)
            return;
        Block block = event.getBlock();
        Recipe furnaceRecipe = event.getRecipe();
        Keyed keyedRecipe = (Keyed) furnaceRecipe;
        ItemStack source = event.getSource();
        CustomItemType itemType = CustomItemType.get(source);
        NamespacedKey recipeKey = keyedRecipe.getKey();
        if (!recipeKey.getNamespace().equals(NamespacedKey.MINECRAFT) || itemType.isCustomItem()) {
            CookingRecipeRegister register = CookingRecipeRegister.getInstanceUnsafe();
            if (register != null && !register.isEmpty()) {
                for (BaseCookingRecipe recipe : register.listAllAssociatedWithDummyRecipe(recipeKey)) {
                    if (itemType.equals(recipe.getOriginal()) && recipe.filterAcceptedBlock(block)) {
                        event.setTotalCookTime(source.getAmount() * recipe.getCookingTime());
                        break;
                    }
                }
            }
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(priority = EventPriority.LOW)
    public void listenCampfireStartCook(CampfireStartEvent event) {
        if (event == null)
            return;
        Block block = event.getBlock();
        CampfireRecipe campfireRecipe = event.getRecipe();
        CustomItemType itemType = CustomItemType.get(event.getSource());
        NamespacedKey recipeKey = campfireRecipe.getKey();
        if (!recipeKey.getNamespace().equals(NamespacedKey.MINECRAFT) || itemType.isCustomItem()) {
            CookingRecipeRegister register = CookingRecipeRegister.getInstanceUnsafe();
            if (register != null && !register.isEmpty()) {
                for (BaseCookingRecipe recipe : register.listAllAssociatedWithDummyRecipe(recipeKey)) {
                    if (itemType.equals(recipe.getOriginal()) && recipe.filterAcceptedBlock(block)) {
                        event.setTotalCookTime(recipe.getCookingTime());
                        break;
                    }
                }
            }
        }
    }

}
