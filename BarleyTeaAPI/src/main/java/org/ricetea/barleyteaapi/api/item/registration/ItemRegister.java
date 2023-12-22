package org.ricetea.barleyteaapi.api.item.registration;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.abstracts.IRegister;
import org.ricetea.barleyteaapi.api.event.ItemsRegisteredEvent;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemTick;
import org.ricetea.barleyteaapi.internal.task.ItemTickTask;
import org.ricetea.utils.CollectionUtil;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.logging.Logger;

public final class ItemRegister implements IRegister<BaseItem> {
    @Nonnull
    private static final Lazy<ItemRegister> inst = Lazy.create(ItemRegister::new);

    @Nonnull
    private final AtomicInteger itemNeedTick = new AtomicInteger(0);

    @Nonnull
    private final ConcurrentHashMap<NamespacedKey, BaseItem> lookupTable = new ConcurrentHashMap<>();

    private ItemRegister() {
    }

    @Nonnull
    public static ItemRegister getInstance() {
        BarleyTeaAPI.checkPluginUsable();
        return inst.get();
    }

    @Nullable
    public static ItemRegister getInstanceUnsafe() {
        return inst.getUnsafe();
    }

    public void register(@Nullable BaseItem item) {
        if (item == null)
            return;
        lookupTable.put(item.getKey(), item);
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                Logger logger = inst.getLogger();
                logger.info("registered " + item.getKey() + " as item!");
                if (item instanceof FeatureItemTick && itemNeedTick.getAndIncrement() == 0) {
                    ItemTickTask.getInstance().start();
                }
                Bukkit.getPluginManager().callEvent(new ItemsRegisteredEvent(List.of(item)));
            }
        }
    }

    public void unregister(@Nullable BaseItem item) {
        if (item == null || !lookupTable.remove(item.getKey(), item))
            return;
        BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
        if (inst != null) {
            Logger logger = inst.getLogger();
            logger.info("unregistered " + item.getKey());
            if (item instanceof FeatureItemTick && itemNeedTick.decrementAndGet() == 0) {
                ItemTickTask.getInstance().stop();
            }
            Bukkit.getPluginManager().callEvent(new ItemsRegisteredEvent(List.of(item)));
        }
    }

    @Override
    public void unregisterAll() {
        var keySet = Collections.unmodifiableSet(lookupTable.keySet());
        var values = CollectionUtil.toUnmodifiableList(lookupTable.values());
        lookupTable.clear();
        Logger logger = ObjectUtil.safeMap(BarleyTeaAPI.getInstanceUnsafe(), BarleyTeaAPI::getLogger);
        if (logger != null) {
            for (NamespacedKey key : keySet) {
                logger.info("unregistered " + key.getKey());
            }
            Bukkit.getPluginManager().callEvent(new ItemsRegisteredEvent(values));
        }
    }

    @Override
    public void unregisterAll(@Nullable Predicate<BaseItem> predicate) {
        if (predicate == null)
            unregisterAll();
        else {
            for (BaseItem item : listAll(predicate)) {
                unregister(item);
            }
        }
    }

    @Nullable
    public BaseItem lookup(@Nullable NamespacedKey key) {
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
        return !lookupTable.isEmpty();
    }

    public boolean hasAnyRegisteredNeedTicking() {
        return itemNeedTick.get() > 0;
    }

    @Override
    @Nonnull
    public Collection<BaseItem> listAll() {
        return ObjectUtil.letNonNull(Collections.unmodifiableCollection(lookupTable.values()),
                Collections::emptySet);
    }

    @Override
    @Nonnull
    public Collection<BaseItem> listAll(@Nullable Predicate<BaseItem> predicate) {
        return predicate == null ? listAll()
                : ObjectUtil.letNonNull(
                lookupTable.values().stream()
                        .filter(predicate)
                        .toList(),
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
    public Collection<NamespacedKey> listAllKeys(@Nullable Predicate<BaseItem> predicate) {
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
    public BaseItem findFirst(@Nullable Predicate<BaseItem> predicate) {
        var stream = lookupTable.values().stream();
        if (predicate != null)
            stream = stream.filter(predicate);
        return stream.findFirst().orElse(null);
    }

    @Override
    @Nullable
    public NamespacedKey findFirstKey(@Nullable Predicate<BaseItem> predicate) {
        var stream = lookupTable.entrySet().stream();
        if (predicate != null)
            stream = stream.filter(new Filter<>(predicate));
        return stream.map(new Mapper<>()).findFirst().orElse(null);
    }
}
