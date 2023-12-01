package org.ricetea.barleyteaapi.api.item.registration;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;
import java.util.logging.Logger;
import java.util.stream.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.abstracts.IRegister;
import org.ricetea.barleyteaapi.api.item.recipe.BaseRecipe;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.ObjectUtil;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

abstract class RecipeRegister<T extends BaseRecipe> implements IRegister<T> {

    @Nonnull
    private final Hashtable<NamespacedKey, T> lookupTable = new Hashtable<>();

    @Nonnull
    private final Multimap<NamespacedKey, NamespacedKey> collidingTable = Objects
            .requireNonNull(LinkedHashMultimap.create());

    @Nonnull
    private final Hashtable<NamespacedKey, NamespacedKey> collidingTable_revert = new Hashtable<>();

    @Nonnull
    private final AtomicInteger flowNumber = new AtomicInteger(0);

    @Nonnull
    private final String dummyRecipePrefix;

    protected RecipeRegister(@Nonnull String dummyRecipePrefix) {
        this.dummyRecipePrefix = dummyRecipePrefix;
    }

    @Nonnull
    protected NamespacedKey nextDummyRecipeKey() {
        return NamespacedKeyUtil.BarleyTeaAPI(dummyRecipePrefix + "_" + flowNumber.getAndIncrement());
    }

    @Override
    public void register(@Nullable T recipe) {
        if (recipe == null)
            return;
        NamespacedKey recipeKey = recipe.getKey();
        if (lookupTable.put(recipeKey, recipe) != null)
            unlinkMap(recipeKey);
        NamespacedKey dummyKey = findDummyRecipeKey(recipe);
        if (dummyKey == null) {
            dummyKey = nextDummyRecipeKey();
            createDummyRecipe(recipe, dummyKey);
        }
        linkMap(recipeKey, dummyKey);
        afterRegisterRecipe(recipe);
    }

    @Override
    public void unregister(@Nullable T recipe) {
        if (recipe == null)
            return;
        NamespacedKey recipeKey = recipe.getKey();
        if (!lookupTable.remove(recipeKey, recipe))
            return;
        unlinkMap(recipeKey);
        afterUnregisterRecipe(recipe);
    }

    @Override
    public void unregisterAll() {
        var keySet = Collections.unmodifiableSet(lookupTable.keySet());
        lookupTable.clear();
        collidingTable_revert.clear();
        collidingTable.keySet().forEach(key -> {
            if (key.getNamespace().equals(NamespacedKeyUtil.BarleyTeaAPI) &&
                    key.getKey().startsWith(dummyRecipePrefix)) {
                Bukkit.removeRecipe(key);
            }
        });
        collidingTable.clear();
        Logger logger = ObjectUtil.mapWhenNonnull(BarleyTeaAPI.getInstanceUnsafe(), BarleyTeaAPI::getLogger);
        if (logger != null) {
            for (NamespacedKey key : keySet) {
                afterUnregisterRecipe(logger, Objects.requireNonNull(key));
            }
        }
    }

    @Override
    public void unregisterAll(@Nullable Predicate<T> predicate) {
        if (predicate == null)
            unregisterAll();
        else {
            for (T item : listAll(predicate)) {
                unregister(item);
            }
        }
    }

    @Nullable
    public T lookup(@Nullable NamespacedKey key) {
        if (key == null)
            return null;
        return lookupTable.get(key);
    }

    public boolean has(@Nullable NamespacedKey key) {
        if (key == null)
            return false;
        return lookupTable.containsKey(key);
    }

    public boolean hasAnyRegistered() {
        return lookupTable.size() > 0;
    }

    @Override
    @Nonnull
    public Collection<T> listAll() {
        return ObjectUtil.letNonNull(Collections.unmodifiableCollection(lookupTable.values()),
                Collections::emptySet);
    }

    @Override
    @Nonnull
    public Collection<T> listAll(@Nullable Predicate<T> predicate) {
        return predicate == null ? listAll()
                : ObjectUtil.letNonNull(
                        lookupTable.values().stream().filter(predicate).collect(Collectors.toUnmodifiableList()),
                        Collections::emptySet);
    }

    @Nonnull
    public Collection<T> listAllAssociatedWithDummies(@Nonnull NamespacedKey key) {
        return ObjectUtil.letNonNull(
                collidingTable.get(key).stream().map(this::lookup).collect(Collectors.toUnmodifiableSet()),
                Collections::emptySet);
    }

    @Override
    @Nonnull
    public Collection<NamespacedKey> listAllKeys() {
        return ObjectUtil.letNonNull(Collections.unmodifiableCollection(lookupTable.keySet()),
                Collections::emptySet);
    }

    @Override
    @Nonnull
    public Collection<NamespacedKey> listAllKeys(@Nullable Predicate<T> predicate) {
        return predicate == null ? listAllKeys()
                : ObjectUtil.letNonNull(
                        lookupTable.entrySet().stream().filter(new Filter<>(predicate)).map(new Mapper<>())
                                .collect(Collectors.toUnmodifiableList()),
                        Collections::emptySet);
    }

    @Override
    @Nullable
    public T findFirst(@Nullable Predicate<T> predicate) {
        var stream = lookupTable.values().stream();
        if (predicate != null)
            stream = stream.filter(predicate);
        return stream.findFirst().orElse(null);
    }

    @Override
    @Nullable
    public NamespacedKey findFirstKey(@Nullable Predicate<T> predicate) {
        var stream = lookupTable.entrySet().stream();
        if (predicate != null)
            stream = stream.filter(new Filter<>(predicate));
        return stream.map(new Mapper<>()).findFirst().orElse(null);
    }

    protected void linkMap(@Nonnull NamespacedKey recipeKey, @Nonnull NamespacedKey dummyKey) {
        collidingTable.put(dummyKey, recipeKey);
        collidingTable_revert.put(recipeKey, dummyKey);
    }

    protected void unlinkMap(@Nonnull NamespacedKey recipeKey) {
        NamespacedKey header = collidingTable_revert.remove(recipeKey);
        if (header != null &&
                collidingTable.remove(header, recipeKey) &&
                !collidingTable.containsKey(header) &&
                header.getNamespace().equals(NamespacedKeyUtil.BarleyTeaAPI) &&
                header.getKey().startsWith(dummyRecipePrefix))
            Bukkit.removeRecipe(header);
    }

    @Nullable
    protected abstract NamespacedKey findDummyRecipeKey(@Nonnull T recipe);

    protected void createDummyRecipe(@Nonnull T recipe, @Nonnull NamespacedKey dummyKey) {
        Recipe bukkitRecipe = recipe.toBukkitRecipe(dummyKey);
        Bukkit.addRecipe(bukkitRecipe);
    }

    protected void afterRegisterRecipe(@Nonnull T recipe) {
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                Logger logger = inst.getLogger();
                afterRegisterRecipe(logger, recipe.getKey());
            }
        }
    }

    protected void afterUnregisterRecipe(@Nonnull T recipe) {
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                Logger logger = inst.getLogger();
                afterUnregisterRecipe(logger, recipe.getKey());
            }
        }
    }

    private void afterRegisterRecipe(Logger logger, @Nonnull NamespacedKey key) {
        logger.info("registered " + key.toString());
    }

    private void afterUnregisterRecipe(Logger logger, @Nonnull NamespacedKey key) {
        logger.info("unregistered " + key.toString());
    }
}
