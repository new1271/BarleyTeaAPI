package org.ricetea.barleyteaapi.api.item.registration;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.abstracts.IRegister;
import org.ricetea.barleyteaapi.api.item.recipe.BaseCookingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.BlastingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.CampfireRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.FurnaceRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.SmokingRecipe;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;
import org.ricetea.barleyteaapi.util.ObjectUtil;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public final class CookingRecipeRegister implements IRegister<BaseCookingRecipe> {

    @Nonnull
    private static final Lazy<CookingRecipeRegister> inst = new Lazy<>(CookingRecipeRegister::new);

    @Nonnull
    private final Hashtable<NamespacedKey, BaseCookingRecipe> lookupTable = new Hashtable<>();

    @Nonnull
    private final Multimap<NamespacedKey, NamespacedKey> collidingTable = Objects
            .requireNonNull(LinkedHashMultimap.create());

    @Nonnull
    private final Multimap<NamespacedKey, NamespacedKey> collidingTable_revert = Objects
            .requireNonNull(LinkedHashMultimap.create());

    @Nonnull
    private final AtomicInteger flowNumber = new AtomicInteger(0);

    private CookingRecipeRegister() {
    }

    @Nonnull
    public static CookingRecipeRegister getInstance() {
        BarleyTeaAPI.checkPluginUsable();
        return inst.get();
    }

    @Nullable
    public static CookingRecipeRegister getInstanceUnsafe() {
        return inst.getUnsafe();
    }

    @Override
    public void register(@Nonnull BaseCookingRecipe recipe) {
        lookupTable.put(recipe.getKey(), recipe);
        int recipeTypeIndex = 0;
        Recipe bukkitRecipe;
        if (recipe instanceof FurnaceRecipe furnaceRecipe) {
            recipeTypeIndex = 1;
            bukkitRecipe = furnaceRecipe
                    .toBukkitRecipe(
                            NamespacedKeyUtils.BarleyTeaAPI("dummy_cooking_recipe_" + flowNumber.getAndIncrement()));
            if (!Bukkit.addRecipe(bukkitRecipe)) {
                ItemStack originalItem = new ItemStack(furnaceRecipe.getOriginal().toMaterial());
                for (var iterator = Bukkit.recipeIterator(); iterator.hasNext();) {
                    Recipe iteratingRecipe = iterator.next();
                    if (iteratingRecipe instanceof org.bukkit.inventory.FurnaceRecipe iteratingFurnaceRecipe) {
                        if (iteratingFurnaceRecipe.getInputChoice().test(originalItem)) {
                            bukkitRecipe = iteratingRecipe;
                            break;
                        }
                    }
                }
            }
        } else if (recipe instanceof SmokingRecipe smokingRecipe) {
            recipeTypeIndex = 2;
            bukkitRecipe = smokingRecipe
                    .toBukkitRecipe(
                            NamespacedKeyUtils.BarleyTeaAPI("dummy_cooking_recipe_" + flowNumber.getAndIncrement()));
            if (!Bukkit.addRecipe(bukkitRecipe)) {
                ItemStack originalItem = new ItemStack(smokingRecipe.getOriginal().toMaterial());
                for (var iterator = Bukkit.recipeIterator(); iterator.hasNext();) {
                    Recipe iteratingRecipe = iterator.next();
                    if (iteratingRecipe instanceof org.bukkit.inventory.SmokingRecipe iteratingFurnaceRecipe) {
                        if (iteratingFurnaceRecipe.getInputChoice().test(originalItem)) {
                            bukkitRecipe = iteratingRecipe;
                            break;
                        }
                    } else if (iteratingRecipe instanceof org.bukkit.inventory.FurnaceRecipe iteratingFurnaceRecipe) {
                        if (iteratingFurnaceRecipe.getInputChoice().test(originalItem)) {
                            bukkitRecipe = iteratingRecipe;
                            break;
                        }
                    }
                }
            }
        } else if (recipe instanceof BlastingRecipe blastingRecipe) {
            recipeTypeIndex = 3;
            bukkitRecipe = blastingRecipe
                    .toBukkitRecipe(
                            NamespacedKeyUtils.BarleyTeaAPI("dummy_cooking_recipe_" + flowNumber.getAndIncrement()));
            if (!Bukkit.addRecipe(bukkitRecipe)) {
                ItemStack originalItem = new ItemStack(blastingRecipe.getOriginal().toMaterial());
                for (var iterator = Bukkit.recipeIterator(); iterator.hasNext();) {
                    Recipe iteratingRecipe = iterator.next();
                    if (iteratingRecipe instanceof org.bukkit.inventory.BlastingRecipe iteratingFurnaceRecipe) {
                        if (iteratingFurnaceRecipe.getInputChoice().test(originalItem)) {
                            bukkitRecipe = iteratingRecipe;
                            break;
                        }
                    } else if (iteratingRecipe instanceof org.bukkit.inventory.FurnaceRecipe iteratingFurnaceRecipe) {
                        if (iteratingFurnaceRecipe.getInputChoice().test(originalItem)) {
                            bukkitRecipe = iteratingRecipe;
                            break;
                        }
                    }
                }
            }
        } else if (recipe instanceof CampfireRecipe campfireRecipe) {
            recipeTypeIndex = 4;
            bukkitRecipe = campfireRecipe
                    .toBukkitRecipe(
                            NamespacedKeyUtils.BarleyTeaAPI("dummy_cooking_recipe_" + flowNumber.getAndIncrement()));
            if (!Bukkit.addRecipe(bukkitRecipe)) {
                ItemStack originalItem = new ItemStack(campfireRecipe.getOriginal().toMaterial());
                for (var iterator = Bukkit.recipeIterator(); iterator.hasNext();) {
                    Recipe iteratingRecipe = iterator.next();
                    if (iteratingRecipe instanceof org.bukkit.inventory.CampfireRecipe iteratingCampfireRecipe) {
                        if (iteratingCampfireRecipe.getInputChoice().test(originalItem)) {
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
                        logger.info("registered " + recipe.getKey().toString() + " as furnace recipe!");
                        break;
                    case 2:
                        logger.info("registered " + recipe.getKey().toString() + " as smoker recipe!");
                        break;
                    case 3:
                        logger.info("registered " + recipe.getKey().toString() + " as blast-furnace recipe!");
                        break;
                    case 4:
                        logger.info("registered " + recipe.getKey().toString() + " as campfire recipe!");
                        break;
                    default:
                        logger.warning(
                                "registered " + recipe.getKey().toString() + " as unknown-type cooking recipe!");
                        break;
                }
            }
        }
    }

    @Override
    public void unregister(@Nonnull BaseCookingRecipe recipe) {
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
    public BaseCookingRecipe lookup(@Nonnull NamespacedKey key) {
        return lookupTable.get(key);
    }

    public boolean has(@Nonnull NamespacedKey key) {
        return lookupTable.containsKey(key);
    }

    public boolean hasAnyRegistered() {
        return lookupTable.size() > 0;
    }

    @Override
    @Nonnull
    public Collection<BaseCookingRecipe> listAll() {
        return ObjectUtil.letNonNull(Collections.unmodifiableCollection(lookupTable.values()),
                Collections::emptySet);
    }

    @Override
    @Nonnull
    public Collection<BaseCookingRecipe> listAll(@Nullable Predicate<BaseCookingRecipe> predicate) {
        return predicate == null ? listAll()
                : ObjectUtil.letNonNull(
                        lookupTable.values().stream().filter(predicate).collect(Collectors.toUnmodifiableList()),
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
    public Collection<NamespacedKey> listAllKeys(@Nullable Predicate<BaseCookingRecipe> predicate) {
        return predicate == null ? listAllKeys()
                : ObjectUtil.letNonNull(
                        lookupTable.entrySet().stream().filter(new Filter<>(predicate)).map(new Mapper<>())
                                .collect(Collectors.toUnmodifiableList()),
                        Collections::emptySet);
    }

    @Nonnull
    public Collection<BaseCookingRecipe> listAllAssociatedWithDummies(@Nonnull NamespacedKey key) {
        return ObjectUtil.letNonNull(
                collidingTable.get(key).stream().map(this::lookup).collect(Collectors.toUnmodifiableSet()),
                Collections::emptySet);
    }

    @Override
    @Nullable
    public BaseCookingRecipe findFirst(@Nullable Predicate<BaseCookingRecipe> predicate) {
        var stream = lookupTable.values().stream();
        if (predicate != null)
            stream = stream.filter(predicate);
        return stream.findFirst().orElse(null);
    }

    @Override
    @Nullable
    public NamespacedKey findFirstKey(@Nullable Predicate<BaseCookingRecipe> predicate) {
        var stream = lookupTable.entrySet().stream();
        if (predicate != null)
            stream = stream.filter(new Filter<>(predicate));
        return stream.map(new Mapper<>()).findFirst().orElse(null);
    }
}
