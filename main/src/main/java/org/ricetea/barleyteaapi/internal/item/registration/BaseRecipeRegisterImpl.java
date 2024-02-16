package org.ricetea.barleyteaapi.internal.item.registration;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.base.registration.RecipeKeyedRegister;
import org.ricetea.barleyteaapi.api.item.recipe.BaseRecipe;
import org.ricetea.barleyteaapi.internal.base.registration.NSKeyedRegisterBase;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
@ApiStatus.Internal
abstract class BaseRecipeRegisterImpl<T extends BaseRecipe> extends NSKeyedRegisterBase<T> implements RecipeKeyedRegister<T> {

    @Nonnull
    private final Multimap<NamespacedKey, NamespacedKey> collidingTable = Multimaps.synchronizedSetMultimap(LinkedHashMultimap.create());

    @Nonnull
    private final ConcurrentHashMap<NamespacedKey, NamespacedKey> collidingTable_revert = new ConcurrentHashMap<>();

    @Nonnull
    private final AtomicInteger flowNumber = new AtomicInteger(0);

    @Nonnull
    private final String dummyRecipePrefix;

    protected BaseRecipeRegisterImpl(@Nonnull String dummyRecipePrefix) {
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
        if (getLookupMap().put(recipeKey, recipe) != null)
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
        if (!getLookupMap().remove(recipeKey, recipe))
            return;
        unlinkMap(recipeKey);
        afterUnregisterRecipe(recipe);
    }

    @Override
    public void unregisterAll() {
        Map<NamespacedKey, T> lookupMap = getLookupMap();
        var keySet = Collections.unmodifiableSet(lookupMap.keySet());
        lookupMap.clear();
        collidingTable_revert.clear();
        collidingTable.keySet().forEach(key -> {
            if (key.getNamespace().equals(NamespacedKeyUtil.BarleyTeaAPI) &&
                    key.getKey().startsWith(dummyRecipePrefix)) {
                Bukkit.removeRecipe(key);
            }
        });
        collidingTable.clear();
        Logger logger = ObjectUtil.safeMap(BarleyTeaAPI.getInstanceUnsafe(), BarleyTeaAPI::getLogger);
        if (logger != null) {
            for (NamespacedKey key : keySet) {
                afterUnregisterRecipe(logger, Objects.requireNonNull(key));
            }
        }
    }

    @Override
    public void unregisterAll(@Nullable Predicate<? super T> predicate) {
        if (predicate == null)
            unregisterAll();
        else {
            for (T item : listAll(predicate)) {
                unregister(item);
            }
        }
    }

    @Nonnull
    public Collection<T> listAllAssociatedWithDummyRecipe(@Nonnull NamespacedKey key) {
        return ObjectUtil.letNonNull(
                collidingTable.get(key).stream()
                        .map(this::lookup)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toUnmodifiableSet()),
                Collections::emptySet);
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

    private void afterRegisterRecipe(@Nonnull Logger logger, @Nonnull NamespacedKey key) {
        logger.info(LOGGING_REGISTERED_FORMAT.formatted(key, "recipe"));
    }

    private void afterUnregisterRecipe(@Nonnull Logger logger, @Nonnull NamespacedKey key) {
        logger.info(LOGGING_UNREGISTERED_FORMAT.formatted(key));
    }
}
