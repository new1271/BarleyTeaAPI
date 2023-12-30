package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingInventory;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.item.CustomItemType;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.recipe.BaseSmithingRecipe;
import org.ricetea.barleyteaapi.internal.item.registration.SmithingRecipeRegisterImpl;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class SmithingListener implements Listener {

    private static final Lazy<SmithingListener> inst = Lazy.create(SmithingListener::new);

    private SmithingListener() {
    }

    @Nonnull
    public static SmithingListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemSmithing(PrepareSmithingEvent event) {
        if (event == null)
            return;
        SmithingInventory inventory = event.getInventory();
        Recipe smithingRecipe = inventory.getRecipe();
        if (smithingRecipe == null)
            return;
        if (smithingRecipe instanceof Keyed keyedRecipe) {
            NamespacedKey recipeKey = keyedRecipe.getKey();
            ItemStack original = inventory.getInputEquipment();
            ItemStack template = inventory.getInputTemplate();
            ItemStack addition = inventory.getInputMineral();
            if (original == null || template == null || addition == null)
                return;
            CustomItemType originalType = CustomItemType.get(original);
            CustomItemType templateType = CustomItemType.get(template);
            CustomItemType additionType = CustomItemType.get(addition);
            original = ItemHelper.getSingletonClone(original);
            template = ItemHelper.getSingletonClone(template);
            addition = ItemHelper.getSingletonClone(addition);
            if (!recipeKey.getNamespace().equals(NamespacedKey.MINECRAFT) || originalType.isCustomItem()
                    || templateType.isCustomItem() || additionType.isCustomItem()) {
                final ItemStack oldResult = event.getResult();
                ItemStack result = oldResult;
                boolean allPassed = true;
                SmithingRecipeRegisterImpl register = SmithingRecipeRegisterImpl.getInstanceUnsafe();
                if (register != null && register.hasAnyRegistered()) {
                    for (BaseSmithingRecipe recipe : register.listAllAssociatedWithDummies(recipeKey)) {
                        if (recipe.getOriginal().equals(originalType) && recipe.filterAdditionType(additionType)
                                && recipe.filterTemplateType(templateType)) {
                            result = recipe.apply(original, template, addition);
                            allPassed = false;
                            break;
                        }
                    }
                }
                if (allPassed) {
                    result = null;
                }
                if (oldResult != result) {
                    event.setResult(result);
                }
            }
        }
    }
}
