package org.ricetea.barleyteaapi.api.item.registration;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.abstracts.IRegister;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureCommandGive;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemTick;
import org.ricetea.barleyteaapi.internal.nms.NMSBaseCommand;
import org.ricetea.barleyteaapi.internal.task.ItemTickTask;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

public final class ItemRegister implements IRegister<BaseItem> {
    @Nonnull
    private static final Lazy<ItemRegister> inst = Lazy.create(ItemRegister::new);

    @Nonnull
    private AtomicInteger itemNeedTick = new AtomicInteger(0);

    @Nonnull
    private final Hashtable<NamespacedKey, BaseItem> lookupTable = new Hashtable<>();

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

    public void register(@Nonnull BaseItem item) {
        lookupTable.put(item.getKey(), item);
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstance();
            if (inst != null) {
                Logger logger = inst.getLogger();
                logger.info("registered " + item.getKey().toString() + " as item!");
                if (item instanceof FeatureCommandGive) {
                    ObjectUtil.callWhenNonnull(inst.giveCommand, NMSBaseCommand::update);
                }
                if (item instanceof FeatureItemTick && itemNeedTick.getAndIncrement() == 0) {
                    ItemTickTask.getInstance().start();
                }
            }
        }
    }

    public void unregister(@Nonnull BaseItem item) {
        lookupTable.remove(item.getKey());
        BarleyTeaAPI inst = BarleyTeaAPI.getInstance();
        if (inst != null) {
            Logger logger = inst.getLogger();
            logger.info("unregistered " + item.getKey().toString());
            if (item instanceof FeatureCommandGive) {
                ObjectUtil.callWhenNonnull(inst.giveCommand, NMSBaseCommand::update);
            }
            if (item instanceof FeatureItemTick && itemNeedTick.decrementAndGet() == 0) {
                ItemTickTask.getInstance().stop();
            }
        }
    }

    @Nullable
    public BaseItem lookup(@Nonnull NamespacedKey key) {
        return lookupTable.get(key);
    }

    public boolean has(@Nonnull NamespacedKey key) {
        return lookupTable.containsKey(key);
    }

    public boolean hasAnyRegistered() {
        return lookupTable.size() > 0;
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
    public Collection<NamespacedKey> listAllKeys(@Nullable Predicate<BaseItem> predicate) {
        return predicate == null ? listAllKeys()
                : ObjectUtil.letNonNull(
                        lookupTable.entrySet().stream().filter(new Filter<>(predicate)).map(new Mapper<>())
                                .collect(Collectors.toUnmodifiableList()),
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
