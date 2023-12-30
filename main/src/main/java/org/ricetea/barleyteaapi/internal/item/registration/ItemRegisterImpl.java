package org.ricetea.barleyteaapi.internal.item.registration;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.event.ItemsRegisteredEvent;
import org.ricetea.barleyteaapi.api.event.ItemsUnregisteredEvent;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityMove;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemTick;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.internal.item.CustomItemTypeImpl;
import org.ricetea.barleyteaapi.internal.task.ItemTickTask;
import org.ricetea.utils.CollectionUtil;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.logging.Logger;

@Singleton
@ApiStatus.Internal
public final class ItemRegisterImpl implements ItemRegister {
    @Nonnull
    private static final Lazy<ItemRegisterImpl> inst = Lazy.create(ItemRegisterImpl::new);

    @Nonnull
    private final AtomicInteger itemNeedTick = new AtomicInteger(0);
    @Nonnull
    private final AtomicInteger itemNeedMovingFeature = new AtomicInteger(0);

    @Nonnull
    private final ConcurrentHashMap<NamespacedKey, CustomItem> lookupTable = new ConcurrentHashMap<>();

    private ItemRegisterImpl() {
    }

    @Nonnull
    public static ItemRegisterImpl getInstance() {
        return inst.get();
    }

    @Nullable
    public static ItemRegisterImpl getInstanceUnsafe() {
        return inst.getUnsafe();
    }

    public void register(@Nullable CustomItem item) {
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
                if (item instanceof FeatureItemHoldEntityMove) {
                    itemNeedMovingFeature.getAndIncrement();
                }
                Bukkit.getPluginManager().callEvent(new ItemsRegisteredEvent(List.of(item)));
            }
        }
    }

    public void unregister(@Nullable CustomItem item) {
        if (item == null)
            return;
        unregister0(item);
        List<CustomItem> list = List.of(item);
        Bukkit.getPluginManager().callEvent(new ItemsUnregisteredEvent(list));
        CustomItemTypeImpl.removeInstances(list);
    }

    public void unregister0(@Nullable CustomItem item) {
        if (item == null || !lookupTable.remove(item.getKey(), item))
            return;
        BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
        if (inst != null) {
            Logger logger = inst.getLogger();
            logger.info("unregistered " + item.getKey());
            if (item instanceof FeatureItemTick && itemNeedTick.decrementAndGet() == 0) {
                ItemTickTask.getInstance().stop();
            }
            if (item instanceof FeatureItemHoldEntityMove) {
                itemNeedMovingFeature.getAndDecrement();
            }
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
            Bukkit.getPluginManager().callEvent(new ItemsUnregisteredEvent(values));
            CustomItemTypeImpl.removeInstances(values);
        }
    }

    @Override
    public void unregisterAll(@Nullable Predicate<CustomItem> predicate) {
        if (predicate == null)
            unregisterAll();
        else {
            var items = listAll(predicate);
            for (CustomItem item : items) {
                unregister0(item);
            }
            Bukkit.getPluginManager().callEvent(new ItemsUnregisteredEvent(items));
            CustomItemTypeImpl.removeInstances(items);
        }
    }

    @Nullable
    public CustomItem lookup(@Nullable NamespacedKey key) {
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

    public boolean hasAnyRegisteredNeedMovingFeature() {
        return itemNeedMovingFeature.get() > 0;
    }

    @Override
    @Nonnull
    public Collection<CustomItem> listAll() {
        return ObjectUtil.letNonNull(Collections.unmodifiableCollection(lookupTable.values()),
                Collections::emptySet);
    }

    @Override
    @Nonnull
    public Collection<CustomItem> listAll(@Nullable Predicate<CustomItem> predicate) {
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
    public Collection<NamespacedKey> listAllKeys(@Nullable Predicate<CustomItem> predicate) {
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
    public CustomItem findFirst(@Nullable Predicate<CustomItem> predicate) {
        var stream = lookupTable.values().stream();
        if (predicate != null)
            stream = stream.filter(predicate);
        return stream.findFirst().orElse(null);
    }

    @Override
    @Nullable
    public NamespacedKey findFirstKey(@Nullable Predicate<CustomItem> predicate) {
        var stream = lookupTable.entrySet().stream();
        if (predicate != null)
            stream = stream.filter(new Filter<>(predicate));
        return stream.map(new Mapper<>()).findFirst().orElse(null);
    }
}
