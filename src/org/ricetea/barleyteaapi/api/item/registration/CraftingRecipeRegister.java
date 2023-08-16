package org.ricetea.barleyteaapi.api.item.registration;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.abstracts.IRegister;
import org.ricetea.barleyteaapi.api.item.recipe.BaseCraftingRecipe;
import org.ricetea.barleyteaapi.util.Lazy;

public final class CraftingRecipeRegister implements IRegister<BaseCraftingRecipe> {

    @Nonnull
    private static final Lazy<CraftingRecipeRegister> inst = new Lazy<>(CraftingRecipeRegister::new);

    @Nonnull
    private final Hashtable<NamespacedKey, BaseCraftingRecipe> lookupTable = new Hashtable<>();

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
    }

    @Override
    public void unregister(@Nonnull BaseCraftingRecipe recipe) {
        lookupTable.remove(recipe.getKey());
    }

    @Nullable
    public BaseCraftingRecipe lookupCraftingRecipe(@Nonnull NamespacedKey key) {
        return lookupTable.get(key);
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
