package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ComplexRecipe;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.recipe.BaseCraftingRecipe;
import org.ricetea.barleyteaapi.api.item.registration.CraftingRecipeRegister;
import org.ricetea.barleyteaapi.internal.linker.ItemFeatureLinker;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
@ApiStatus.Internal
public final class CraftListener implements Listener {

    private static final Lazy<CraftListener> inst = Lazy.create(CraftListener::new);

    private CraftListener() {
    }

    @Nonnull
    public static CraftListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemCrafting(PrepareItemCraftEvent event) {
        if (event == null || event.getRecipe() == null)
            return;
        CraftingInventory inventory = event.getInventory();
        ItemStack[] craftingMatrix = Arrays.stream(inventory.getMatrix()).map(ItemHelper::getSingletonClone)
                .toArray(ItemStack[]::new);
        int craftingMatrixLength = craftingMatrix.length;
        CustomItemType[] craftingTypeOfMatrix = new CustomItemType[craftingMatrixLength];
        boolean hasCustomItem = false;
        int availableItemCount = 0;
        for (int i = 0; i < craftingMatrixLength; i++) {
            ItemStack stack = craftingMatrix[i];
            CustomItemType itemType = CustomItemType.get(stack);
            if (!itemType.isEmpty()) {
                if (itemType.isCustomItem()) {
                    hasCustomItem = true;
                }
                if (stack.getAmount() > 1)
                    stack.setAmount(1);
                availableItemCount++;
            }
            craftingTypeOfMatrix[i] = itemType;
        }
        Recipe craftingRecipe = event.getRecipe();
        if (craftingRecipe instanceof Keyed keyedRecipe) {
            NamespacedKey recipeKey = keyedRecipe.getKey();
            if (!recipeKey.getNamespace().equals(NamespacedKey.MINECRAFT) || hasCustomItem) {
                final ItemStack oldResult = inventory.getResult();
                ItemStack result = oldResult;
                boolean allPassed = true;
                CraftingRecipeRegister register = CraftingRecipeRegister.getInstanceUnsafe();
                if (register != null && !register.isEmpty()) {
                    for (BaseCraftingRecipe recipe : register.listAllAssociatedWithDummyRecipe(recipeKey)) {
                        if (recipe.checkMatrixOfTypes(craftingTypeOfMatrix)) {
                            result = recipe.apply(craftingMatrix);
                            allPassed = false;
                            break;
                        }
                    }
                }
                if (allPassed && craftingRecipe instanceof ComplexRecipe) {
                    switch (recipeKey.getNamespace()) {
                        case NamespacedKey.MINECRAFT -> {
                            switch (recipeKey.getKey()) {
                                case "repair_item" -> { //Item Repair
                                    if (availableItemCount == 2) {
                                        ItemStack[] stacks = new ItemStack[2];
                                        CustomItem type = null;
                                        for (int i = 0, j = 0; i < 2 && j < craftingMatrixLength; j++) {
                                            CustomItemType craftingType = craftingTypeOfMatrix[j];
                                            if (!craftingType.isEmpty()) {
                                                if (i == 0) {
                                                    if (craftingType.isCustomItem()) {
                                                        stacks[i] = craftingMatrix[j];
                                                        type = craftingType.asCustomItem();
                                                        i++;
                                                    } else {
                                                        break;
                                                    }
                                                } else {
                                                    if (type != null && type.equals(craftingType.asCustomItem())) {
                                                        stacks[i] = craftingMatrix[j];
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                        if (type == null || stacks[1] == null) {
                                            result = null;
                                        } else {
                                            result = ItemFeatureLinker.doItemRepair(stacks[0], stacks[1], null);
                                            allPassed = false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (allPassed) {
                    result = null;
                }
                if (oldResult != result) {
                    inventory.setResult(result);
                }
            }
        }
    }
}
