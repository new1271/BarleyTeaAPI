package org.ricetea.barleyteaapi.api.item.registration;

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.abstracts.IRegister;
import org.ricetea.barleyteaapi.api.item.recipe.BaseCraftingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.ShapedCraftingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.ShapelessCraftingRecipe;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;
import org.ricetea.barleyteaapi.util.ObjectUtil;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public final class CraftingRecipeRegister implements IRegister<BaseCraftingRecipe> {

    @Nonnull
    private static final Lazy<CraftingRecipeRegister> inst = new Lazy<>(CraftingRecipeRegister::new);

    @Nonnull
    private final Hashtable<NamespacedKey, BaseCraftingRecipe> lookupTable = new Hashtable<>();

    @Nonnull
    private final Multimap<NamespacedKey, NamespacedKey> collidingTable = ObjectUtil
            .throwWhenNull(LinkedHashMultimap.create());

    @Nonnull
    private final Multimap<NamespacedKey, NamespacedKey> collidingTable_revert = ObjectUtil
            .throwWhenNull(LinkedHashMultimap.create());

    @Nonnull
    private final AtomicInteger flowNumber = new AtomicInteger(0);

    private CraftingRecipeRegister() {
    }

    @Nonnull
    public static CraftingRecipeRegister getInstance() {
        BarleyTeaAPI.checkPluginUsable();
        return inst.get();
    }

    @Nullable
    public static CraftingRecipeRegister getInstanceUnsafe() {
        return inst.getUnsafe();
    }

    @Override
    public void register(@Nonnull BaseCraftingRecipe recipe) {
        lookupTable.put(recipe.getKey(), recipe);
        int recipeTypeIndex = 0;
        Recipe bukkitRecipe;
        if (recipe instanceof ShapedCraftingRecipe shapedRecipe) {
            recipeTypeIndex = 1;
            bukkitRecipe = shapedRecipe
                    .toBukkitRecipe(
                            NamespacedKeyUtils.BarleyTeaAPI("dummy_crafting_recipe_" + flowNumber.getAndIncrement()));
            if (!Bukkit.addRecipe(bukkitRecipe)) {
                bukkitRecipe = Bukkit.getCraftingRecipe(shapedRecipe.getIngredientMatrix().stream()
                        .map(dt -> dt.mapLeftOrRight(m -> m, d -> d.getMaterialBasedOn())).map(ItemStack::new)
                        .toArray(ItemStack[]::new), Bukkit.getWorlds().get(0));
            }
        } else if (recipe instanceof ShapelessCraftingRecipe shapelessCraftingRecipe) {
            recipeTypeIndex = 2;
            bukkitRecipe = shapelessCraftingRecipe
                    .toBukkitRecipe(
                            NamespacedKeyUtils.BarleyTeaAPI("dummy_crafting_recipe_" + flowNumber.getAndIncrement()));
            if (!Bukkit.addRecipe(bukkitRecipe)) {
                bukkitRecipe = Bukkit.getCraftingRecipe(shapelessCraftingRecipe.getIngredients().stream()
                        .map(dt -> dt.mapLeftOrRight(m -> m, d -> d.getMaterialBasedOn())).map(ItemStack::new)
                        .toArray(ItemStack[]::new), Bukkit.getWorlds().get(0));
            }
        } else {
            bukkitRecipe = null;
        }
        if (bukkitRecipe instanceof Keyed keyed) {
            collidingTable.put(keyed.getKey(), recipe.getKey());
            collidingTable_revert.put(recipe.getKey(), keyed.getKey());
        }
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstance();
            if (inst != null) {
                Logger logger = inst.getLogger();
                switch (recipeTypeIndex) {
                    case 1:
                        logger.info("registered " + recipe.getKey().toString() + " as shaped crafting recipe!");
                        break;
                    case 2:
                        logger.info("registered " + recipe.getKey().toString() + " as shapeless crafting recipe!");
                        break;
                    default:
                        logger.warning(
                                "registered " + recipe.getKey().toString() + " as unknown-type crafting recipe!");
                        break;
                }
            }
        }
    }

    @Override
    public void unregister(@Nonnull BaseCraftingRecipe recipe) {
        lookupTable.remove(recipe.getKey());
        Collection<NamespacedKey> headers = collidingTable_revert.removeAll(recipe.getKey());
        if (headers != null) {
            for (NamespacedKey header : headers) {
                collidingTable.remove(header, recipe);
                if (!collidingTable.containsKey(headers)) {
                    Bukkit.removeRecipe(header);
                }
            }
        }
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstance();
            if (inst != null) {
                Logger logger = inst.getLogger();
                logger.info("unregistered " + recipe.getKey().toString());
            }
        }
    }

    public void unregisterAll() {
        lookupTable.clear();
        collidingTable_revert.clear();
        collidingTable.keySet().forEach(key -> {
            if (key.getNamespace().equals(NamespacedKeyUtils.Namespace)) {
                Bukkit.removeRecipe(key);
            }
        });
        collidingTable.clear();
    }

    @Nullable
    public BaseCraftingRecipe lookupCraftingRecipe(@Nonnull NamespacedKey key) {
        return lookupTable.get(key);
    }

    @Nullable
    public List<BaseCraftingRecipe> lookupCraftingRecipeFromDummies(@Nonnull NamespacedKey key) {
        return collidingTable.get(key).stream().map(this::lookupCraftingRecipe).toList();
    }

    public boolean hasCraftingRecipe(@Nonnull NamespacedKey key) {
        return lookupTable.containsKey(key);
    }

    public boolean hasAnyRegisteredCraftingRecipe() {
        return lookupTable.size() > 0;
    }

    @Nonnull
    public NamespacedKey[] getCraftingRecipeIDs(@Nullable Predicate<BaseCraftingRecipe> filter) {
        NamespacedKey[] result;
        if (filter == null)
            result = lookupTable.keySet().toArray(NamespacedKey[]::new);
        else
            result = lookupTable.entrySet().stream().filter(new CraftingRecipeFilter(filter)).map(e -> e.getKey())
                    .toArray(NamespacedKey[]::new);
        return result != null ? result : new NamespacedKey[0];
    }

    @Nonnull
    public BaseCraftingRecipe[] getCraftingRecipeTypes(@Nullable Predicate<BaseCraftingRecipe> filter) {
        BaseCraftingRecipe[] result;
        if (filter == null)
            result = lookupTable.values().toArray(BaseCraftingRecipe[]::new);
        else
            result = lookupTable.values().stream().filter(filter).toArray(BaseCraftingRecipe[]::new);
        return result != null ? result : new BaseCraftingRecipe[0];
    }

    private static class CraftingRecipeFilter implements Predicate<Map.Entry<NamespacedKey, BaseCraftingRecipe>> {

        @Nonnull
        Predicate<BaseCraftingRecipe> filter;

        public CraftingRecipeFilter(@Nonnull Predicate<BaseCraftingRecipe> filter) {
            this.filter = filter;
        }

        @Override
        public boolean test(Entry<NamespacedKey, BaseCraftingRecipe> t) {
            return filter.test(t.getValue());
        }

    }
}
