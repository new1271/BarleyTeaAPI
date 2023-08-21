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
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.abstracts.IRegister;
import org.ricetea.barleyteaapi.api.item.recipe.ArmorTrimSmithingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.BaseSmithingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.SmithingRecipe;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;
import org.ricetea.barleyteaapi.util.ObjectUtil;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public final class SmithingRecipeRegister implements IRegister<BaseSmithingRecipe> {

    @Nonnull
    private static final Lazy<SmithingRecipeRegister> inst = new Lazy<>(SmithingRecipeRegister::new);

    @Nonnull
    private final Hashtable<NamespacedKey, BaseSmithingRecipe> lookupTable = new Hashtable<>();

    @Nonnull
    private final Multimap<NamespacedKey, NamespacedKey> collidingTable = ObjectUtil
            .throwWhenNull(LinkedHashMultimap.create());

    @Nonnull
    private final Multimap<NamespacedKey, NamespacedKey> collidingTable_revert = ObjectUtil
            .throwWhenNull(LinkedHashMultimap.create());

    @Nonnull
    private final AtomicInteger flowNumber = new AtomicInteger(0);

    private SmithingRecipeRegister() {
    }

    @Nonnull
    public static SmithingRecipeRegister getInstance() {
        BarleyTeaAPI.checkPluginUsable();
        return inst.get();
    }

    @Nullable
    public static SmithingRecipeRegister getInstanceUnsafe() {
        return inst.getUnsafe();
    }

    @Override
    public void register(@Nonnull BaseSmithingRecipe recipe) {
        lookupTable.put(recipe.getKey(), recipe);
        int recipeTypeIndex = 0;
        Recipe bukkitRecipe;
        if (recipe instanceof SmithingRecipe smithingRecipe) {
            recipeTypeIndex = 1;
            bukkitRecipe = smithingRecipe.toBukkitRecipe(
                    NamespacedKeyUtils.BarleyTeaAPI("dummy_smithing_recipe_" + flowNumber.getAndIncrement()));
            if (!Bukkit.addRecipe(bukkitRecipe)) {
                ItemStack originalItem = new ItemStack(smithingRecipe.getOriginal().toMaterial());
                ItemStack templateItem = new ItemStack(smithingRecipe.getTemplate().toMaterial());
                ItemStack additionItem = new ItemStack(smithingRecipe.getAddition().toMaterial());
                for (var iterator = Bukkit.recipeIterator(); iterator.hasNext();) {
                    Recipe iteratingRecipe = iterator.next();
                    if (iteratingRecipe instanceof SmithingTransformRecipe iteratingSmithingRecipe) {
                        if (iteratingSmithingRecipe.getBase().test(originalItem)
                                && iteratingSmithingRecipe.getTemplate().test(templateItem)
                                && iteratingSmithingRecipe.getAddition().test(additionItem)) {
                            bukkitRecipe = iteratingRecipe;
                            break;
                        }
                    } else if (iteratingRecipe instanceof SmithingTrimRecipe iteratingSmithingRecipe) {
                        if (iteratingSmithingRecipe.getBase().test(originalItem)
                                && iteratingSmithingRecipe.getTemplate().test(templateItem)
                                && iteratingSmithingRecipe.getAddition().test(additionItem)) {
                            bukkitRecipe = iteratingRecipe;
                            break;
                        }
                    }
                }
            }
        } else if (recipe instanceof ArmorTrimSmithingRecipe smithingRecipe) {
            recipeTypeIndex = 2;
            bukkitRecipe = smithingRecipe.toBukkitRecipe(
                    NamespacedKeyUtils.BarleyTeaAPI("dummy_smithing_recipe_" + flowNumber.getAndIncrement()));
            if (!Bukkit.addRecipe(bukkitRecipe)) {
                ItemStack originalItem = new ItemStack(smithingRecipe.getOriginal().toMaterial());
                ItemStack templateItem = new ItemStack(smithingRecipe.getTemplate().toMaterial());
                ItemStack additionItem = new ItemStack(smithingRecipe.getAddition().toMaterial());
                for (var iterator = Bukkit.recipeIterator(); iterator.hasNext();) {
                    Recipe iteratingRecipe = iterator.next();
                    if (iteratingRecipe instanceof SmithingTransformRecipe iteratingSmithingRecipe) {
                        if (iteratingSmithingRecipe.getBase().test(originalItem)
                                && iteratingSmithingRecipe.getTemplate().test(templateItem)
                                && iteratingSmithingRecipe.getAddition().test(additionItem)) {
                            bukkitRecipe = iteratingRecipe;
                            break;
                        }
                    } else if (iteratingRecipe instanceof SmithingTrimRecipe iteratingSmithingRecipe) {
                        if (iteratingSmithingRecipe.getBase().test(originalItem)
                                && iteratingSmithingRecipe.getTemplate().test(templateItem)
                                && iteratingSmithingRecipe.getAddition().test(additionItem)) {
                            bukkitRecipe = iteratingRecipe;
                            break;
                        }
                    }
                }
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
                        logger.info("registered " + recipe.getKey().toString() + " as normal smithing recipe!");
                        break;
                    case 2:
                        logger.info("registered " + recipe.getKey().toString() + " as armor-trimming smithing recipe!");
                        break;
                    default:
                        logger.warning(
                                "registered " + recipe.getKey().toString() + " as unknown-type smithing recipe!");
                        break;
                }
            }
        }
    }

    @Override
    public void unregister(@Nonnull BaseSmithingRecipe recipe) {
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
    public BaseSmithingRecipe lookupRecipe(@Nonnull NamespacedKey key) {
        return lookupTable.get(key);
    }

    @Nullable
    public List<BaseSmithingRecipe> lookupRecipeFromDummies(@Nonnull NamespacedKey key) {
        return collidingTable.get(key).stream().map(this::lookupRecipe).toList();
    }

    public boolean hasRecipe(@Nonnull NamespacedKey key) {
        return lookupTable.containsKey(key);
    }

    public boolean hasAnyRegisteredRecipe() {
        return lookupTable.size() > 0;
    }

    @Nonnull
    public NamespacedKey[] getRecipeIDs(@Nullable Predicate<BaseSmithingRecipe> filter) {
        NamespacedKey[] result;
        if (filter == null)
            result = lookupTable.keySet().toArray(NamespacedKey[]::new);
        else
            result = lookupTable.entrySet().stream().filter(new RecipeFilter(filter)).map(e -> e.getKey())
                    .toArray(NamespacedKey[]::new);
        return result != null ? result : new NamespacedKey[0];
    }

    @Nonnull
    public BaseSmithingRecipe[] getRecipeTypes(@Nullable Predicate<BaseSmithingRecipe> filter) {
        BaseSmithingRecipe[] result;
        if (filter == null)
            result = lookupTable.values().toArray(BaseSmithingRecipe[]::new);
        else
            result = lookupTable.values().stream().filter(filter).toArray(BaseSmithingRecipe[]::new);
        return result != null ? result : new BaseSmithingRecipe[0];
    }

    private static class RecipeFilter implements Predicate<Map.Entry<NamespacedKey, BaseSmithingRecipe>> {

        @Nonnull
        Predicate<BaseSmithingRecipe> filter;

        public RecipeFilter(@Nonnull Predicate<BaseSmithingRecipe> filter) {
            this.filter = filter;
        }

        @Override
        public boolean test(Entry<NamespacedKey, BaseSmithingRecipe> t) {
            return filter.test(t.getValue());
        }

    }
}
