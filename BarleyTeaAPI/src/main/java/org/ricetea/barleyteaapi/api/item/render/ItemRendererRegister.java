package org.ricetea.barleyteaapi.api.item.render;

import org.bukkit.NamespacedKey;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.abstracts.IRegister;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class ItemRendererRegister implements IRegister<ItemRenderer> {
    @Nonnull
    private static final Lazy<ItemRendererRegister> inst = Lazy.create(ItemRendererRegister::new);

    @Nonnull
    private final Hashtable<NamespacedKey, ItemRenderer> lookupTable = new Hashtable<>();

    private ItemRendererRegister() {
    }

    @Nonnull
    public static ItemRendererRegister getInstance() {
        BarleyTeaAPI.checkPluginUsable();
        return inst.get();
    }

    @Nullable
    public static ItemRendererRegister getInstanceUnsafe() {
        BarleyTeaAPI.checkPluginUsable();
        return inst.getUnsafe();
    }

    @Override
    public void register(@Nullable ItemRenderer renderer) {
        if (renderer == null)
            return;
        lookupTable.put(renderer.getKey(), renderer);
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                Logger logger = inst.getLogger();
                logger.info("registered " + renderer.getKey() + " as item renderer!");
            }
        }
    }

    @Override
    public void unregister(@Nullable ItemRenderer renderer) {
        if (renderer == null)
            return;
        lookupTable.remove(renderer.getKey());
        Logger logger = ObjectUtil.safeMap(BarleyTeaAPI.getInstanceUnsafe(), BarleyTeaAPI::getLogger);
        if (logger != null) {
            logger.info("unregistered " + renderer.getKey());
        }
    }

    @Override
    public void unregisterAll() {
        var keySet = Collections.unmodifiableSet(lookupTable.keySet());
        lookupTable.clear();
        Logger logger = ObjectUtil.safeMap(BarleyTeaAPI.getInstanceUnsafe(), BarleyTeaAPI::getLogger);
        if (logger != null) {
            for (NamespacedKey key : keySet) {
                logger.info("unregistered " + key.getKey());
            }
        }
    }

    @Override
    public void unregisterAll(@Nullable Predicate<ItemRenderer> predicate) {
        if (predicate == null)
            unregisterAll();
        else {
            for (ItemRenderer item : listAll(predicate)) {
                unregister(item);
            }
        }
    }

    @Override
    @Nullable
    public ItemRenderer lookup(@Nullable NamespacedKey key) {
        if (key == null)
            return null;
        return lookupTable.get(key);
    }

    @Nonnull
    public ItemRenderer lookupOrDefault(@Nullable NamespacedKey key,
            @Nonnull ItemRenderer defaultRenderer) {
        if (key == null)
            return defaultRenderer;
        return ObjectUtil.letNonNull(lookup(key), defaultRenderer);
    }

    @Override
    public boolean has(@Nullable NamespacedKey key) {
        if (key == null)
            return false;
        return lookupTable.containsKey(key);
    }

    @Override
    public boolean hasAnyRegistered() {
        return !lookupTable.isEmpty();
    }

    @Override
    @Nonnull
    public Collection<ItemRenderer> listAll() {
        return ObjectUtil.letNonNull(Collections.unmodifiableCollection(lookupTable.values()),
                Collections::emptySet);
    }

    @Override
    @Nonnull
    public Collection<ItemRenderer> listAll(@Nullable Predicate<ItemRenderer> predicate) {
        return predicate == null ? listAll()
                : ObjectUtil.letNonNull(
                        lookupTable.values().stream().filter(predicate).toList(),
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
    public Collection<NamespacedKey> listAllKeys(@Nullable Predicate<ItemRenderer> predicate) {
        return predicate == null ? listAllKeys()
                : ObjectUtil.letNonNull(
                        lookupTable.entrySet().stream()
                                .filter(new Filter<>(predicate))
                                .map(new Mapper<>())
                                .toList(),
                        Collections::emptySet);
    }

    @Override
    @Nullable
    public ItemRenderer findFirst(@Nullable Predicate<ItemRenderer> predicate) {
        var stream = lookupTable.values().stream();
        if (predicate != null)
            stream = stream.filter(predicate);
        return stream.findFirst().orElse(null);
    }

    @Override
    @Nullable
    public NamespacedKey findFirstKey(@Nullable Predicate<ItemRenderer> predicate) {
        var stream = lookupTable.entrySet().stream();
        if (predicate != null)
            stream = stream.filter(new Filter<>(predicate));
        return stream.map(new Mapper<>()).findFirst().orElse(null);
    }
}
