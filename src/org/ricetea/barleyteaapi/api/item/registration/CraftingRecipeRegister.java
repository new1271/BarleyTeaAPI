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
import org.ricetea.barleyteaapi.api.item.recipe.BaseCraftingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.ShapedCraftingRecipe;
import org.ricetea.barleyteaapi.api.item.recipe.ShapelessCraftingRecipe;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public final class CraftingRecipeRegister implements IRegister<BaseCraftingRecipe> {

    @Nonnull
    private static final Lazy<CraftingRecipeRegister> inst = Lazy.create(CraftingRecipeRegister::new);

    @Nonnull
    private final Hashtable<NamespacedKey, BaseCraftingRecipe> lookupTable = new Hashtable<>();

    @Nonnull
    private final Multimap<NamespacedKey, NamespacedKey> collidingTable = Objects
            .requireNonNull(LinkedHashMultimap.create());

    @Nonnull
    private final Multimap<NamespacedKey, NamespacedKey> collidingTable_revert = Objects
            .requireNonNull(LinkedHashMultimap.create());

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
    public BaseCraftingRecipe lookup(@Nonnull NamespacedKey key) {
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
    public Collection<BaseCraftingRecipe> listAll() {
        return ObjectUtil.letNonNull(Collections.unmodifiableCollection(lookupTable.values()),
                Collections::emptySet);
    }

    @Override
    @Nonnull
    public Collection<BaseCraftingRecipe> listAll(@Nullable Predicate<BaseCraftingRecipe> predicate) {
        return predicate == null ? listAll()
                : ObjectUtil.letNonNull(
                        lookupTable.values().stream().filter(predicate).collect(Collectors.toUnmodifiableList()),
                        Collections::emptySet);
    }

    @Nonnull
    public Collection<BaseCraftingRecipe> listAllAssociatedWithDummies(@Nonnull NamespacedKey key) {
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
    public Collection<NamespacedKey> listAllKeys(@Nullable Predicate<BaseCraftingRecipe> predicate) {
        return predicate == null ? listAllKeys()
                : ObjectUtil.letNonNull(
                        lookupTable.entrySet().stream().filter(new Filter<>(predicate)).map(new Mapper<>())
                                .collect(Collectors.toUnmodifiableList()),
                        Collections::emptySet);
    }

    @Override
    @Nullable
    public BaseCraftingRecipe findFirst(@Nullable Predicate<BaseCraftingRecipe> predicate) {
        var stream = lookupTable.values().stream();
        if (predicate != null)
            stream = stream.filter(predicate);
        return stream.findFirst().orElse(null);
    }

    @Override
    @Nullable
    public NamespacedKey findFirstKey(@Nullable Predicate<BaseCraftingRecipe> predicate) {
        var stream = lookupTable.entrySet().stream();
        if (predicate != null)
            stream = stream.filter(new Filter<>(predicate));
        return stream.map(new Mapper<>()).findFirst().orElse(null);
    }
}
