package org.ricetea.barleyteaapi.internal.item.registration;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.registration.ItemSubRendererRegister;
import org.ricetea.barleyteaapi.api.item.registration.ItemSubRendererRegister;
import org.ricetea.barleyteaapi.api.item.render.ItemSubRenderer;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.function.Predicate;
import java.util.logging.Logger;

@Singleton
@ApiStatus.Internal
public final class ItemSubRendererRegisterImpl implements ItemSubRendererRegister {
    @Nonnull
    private static final Lazy<ItemSubRendererRegisterImpl> inst = Lazy.create(ItemSubRendererRegisterImpl::new);

    @Nonnull
    private final Hashtable<NamespacedKey, ItemSubRenderer> lookupTable = new Hashtable<>();

    private ItemSubRendererRegisterImpl() {
    }

    @Nonnull
    public static ItemSubRendererRegisterImpl getInstance() {
        return inst.get();
    }

    @Nullable
    public static ItemSubRendererRegisterImpl getInstanceUnsafe() {
        return inst.getUnsafe();
    }

    @Override
    public void register(@Nullable ItemSubRenderer renderer) {
        if (renderer == null)
            return;
        lookupTable.put(renderer.getKey(), renderer);
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                Logger logger = inst.getLogger();
                logger.info("registered " + renderer.getKey() + " as item sub-renderer!");
            }
        }
    }

    @Override
    public void unregister(@Nullable ItemSubRenderer renderer) {
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
    public void unregisterAll(@Nullable Predicate<ItemSubRenderer> predicate) {
        if (predicate == null)
            unregisterAll();
        else {
            for (ItemSubRenderer item : listAll(predicate)) {
                unregister(item);
            }
        }
    }

    @Override
    @Nullable
    public ItemSubRenderer lookup(@Nullable NamespacedKey key) {
        if (key == null)
            return null;
        return lookupTable.get(key);
    }

    @Nonnull
    public ItemSubRenderer lookupOrDefault(@Nullable NamespacedKey key,
                                        @Nonnull ItemSubRenderer defaultRenderer) {
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
    public Collection<ItemSubRenderer> listAll() {
        return ObjectUtil.letNonNull(Collections.unmodifiableCollection(lookupTable.values()),
                Collections::emptySet);
    }

    @Override
    @Nonnull
    public Collection<ItemSubRenderer> listAll(@Nullable Predicate<ItemSubRenderer> predicate) {
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
    public Collection<NamespacedKey> listAllKeys(@Nullable Predicate<ItemSubRenderer> predicate) {
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
    public ItemSubRenderer findFirst(@Nullable Predicate<ItemSubRenderer> predicate) {
        var stream = lookupTable.values().stream();
        if (predicate != null)
            stream = stream.filter(predicate);
        return stream.findFirst().orElse(null);
    }

    @Override
    @Nullable
    public NamespacedKey findFirstKey(@Nullable Predicate<ItemSubRenderer> predicate) {
        var stream = lookupTable.entrySet().stream();
        if (predicate != null)
            stream = stream.filter(new Filter<>(predicate));
        return stream.map(new Mapper<>()).findFirst().orElse(null);
    }
}
