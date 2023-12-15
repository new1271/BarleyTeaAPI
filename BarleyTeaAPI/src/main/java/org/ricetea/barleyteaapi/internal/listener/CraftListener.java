package org.ricetea.barleyteaapi.internal.listener;

import java.util.Arrays;

import javax.annotation.Nonnull;

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
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.recipe.BaseCraftingRecipe;
import org.ricetea.barleyteaapi.api.item.registration.CraftingRecipeRegister;
import org.ricetea.barleyteaapi.internal.helper.ItemFeatureHelper;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

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
        DataItemType[] craftingTypeOfMatrix = new DataItemType[craftingMatrixLength];
        boolean hasCustomItem = false;
        int availableItemCount = 0;
        for (int i = 0; i < craftingMatrixLength; i++) {
            ItemStack stack = craftingMatrix[i];
            DataItemType itemType = ObjectUtil.letNonNull(ObjectUtil.safeMap(stack, BaseItem::getItemType),
                    DataItemType::empty);
            if (!itemType.isAir()) {
                if (itemType.isRight()) {
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
                if (register != null && register.hasAnyRegistered()) {
                    for (BaseCraftingRecipe recipe : register.listAllAssociatedWithDummies(recipeKey)) {
                        if (recipe.checkMatrixOfTypes(craftingTypeOfMatrix)) {
                            result = recipe.apply(craftingMatrix);
                            allPassed = false;
                            break;
                        }
                    }
                }
                if (allPassed && craftingRecipe instanceof ComplexRecipe complexRecipe) {
                    switch (recipeKey.getNamespace()) {
                        case NamespacedKey.MINECRAFT:
                            switch (recipeKey.getKey()) {
                                case "repair_item": { //Item Repair
                                    if (availableItemCount == 2) {
                                        ItemStack[] stacks = new ItemStack[2];
                                        BaseItem type = null;
                                        for (int i = 0, j = 0; i < 2 && j < craftingMatrixLength; j++) {
                                            DataItemType craftingType = craftingTypeOfMatrix[j];
                                            if (!craftingType.isAir()) {
                                                if (i == 0) {
                                                    if (craftingType.isRight()) {
                                                        stacks[i] = craftingMatrix[j];
                                                        type = craftingType.right();
                                                        i++;
                                                    } else {
                                                        break;
                                                    }
                                                } else {
                                                    if (type != null && type.equals(craftingType.right())) {
                                                        stacks[i] = craftingMatrix[j];
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                        if (type == null || stacks[1] == null) {
                                            result = null;
                                        } else {
                                            result = ItemFeatureHelper.doItemRepair(stacks[0], stacks[1], null);
                                            allPassed = false;
                                        }
                                    }
                                }
                                    break;
                            }
                            break;
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
