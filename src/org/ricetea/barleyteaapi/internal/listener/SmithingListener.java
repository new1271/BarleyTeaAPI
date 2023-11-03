package org.ricetea.barleyteaapi.internal.listener;

import javax.annotation.Nonnull;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingInventory;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemType;
import org.ricetea.barleyteaapi.api.item.recipe.BaseSmithingRecipe;
import org.ricetea.barleyteaapi.api.item.registration.SmithingRecipeRegister;
import org.ricetea.barleyteaapi.api.item.render.AbstractItemRenderer;
import org.ricetea.barleyteaapi.internal.helper.ItemHelper;
import org.ricetea.utils.Lazy;

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
        if (inventory == null)
            return;
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
            DataItemType originalType = BaseItem.getItemType(original);
            DataItemType templateType = BaseItem.getItemType(template);
            DataItemType additionType = BaseItem.getItemType(addition);
            original = ItemHelper.getSingletonClone(original);
            template = ItemHelper.getSingletonClone(template);
            addition = ItemHelper.getSingletonClone(addition);
            if (!recipeKey.getNamespace().equals(NamespacedKey.MINECRAFT) || originalType.isCustom()
                    || templateType.isCustom() || additionType.isCustom()) {
                final ItemStack oldResult = event.getResult();
                ItemStack result = oldResult;
                boolean allPassed = true;
                SmithingRecipeRegister register = SmithingRecipeRegister.getInstanceUnsafe();
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
                    if (BaseItem.isBarleyTeaItem(result)) {
                        AbstractItemRenderer.renderItem(result);
                    }
                    event.setResult(result);
                }
            }
        }
    }
}
