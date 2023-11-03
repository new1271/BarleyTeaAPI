package org.ricetea.barleyteaapi.api.block.registration;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.abstracts.IRegister;
import org.ricetea.barleyteaapi.api.block.BaseBlock;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

public final class BlockRegister implements IRegister<BaseBlock> {
    @Nonnull
    private static final Lazy<BlockRegister> inst = Lazy.create(BlockRegister::new);

    @Nonnull
    private final Hashtable<NamespacedKey, BaseBlock> lookupTable = new Hashtable<>();

    private BlockRegister() {
    }

    @Nonnull
    public static BlockRegister getInstance() {
        BarleyTeaAPI.checkPluginUsable();
        return inst.get();
    }

    @Nullable
    public static BlockRegister getInstanceUnsafe() {
        return inst.getUnsafe();
    }

    public void register(@Nullable BaseBlock block) {
        if (block == null)
            return;
        lookupTable.put(block.getKey(), block);
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstance();
            if (inst != null) {
                Logger logger = inst.getLogger();
                logger.info("registered " + block.getKey().toString() + " as block!");
            }
        }
    }

    public void unregister(@Nullable BaseBlock block) {
        if (block == null)
            return;
        lookupTable.remove(block.getKey());
        BarleyTeaAPI inst = BarleyTeaAPI.getInstance();
        if (inst != null) {
            Logger logger = inst.getLogger();
            logger.info("unregistered " + block.getKey().toString());
        }
    }

    @Nullable
    public BaseBlock lookup(@Nullable NamespacedKey key) {
        if (key == null)
            return null;
        return lookupTable.get(key);
    }

    public boolean has(@Nullable NamespacedKey key) {
        if (key == null)
            return false;
        return lookupTable.containsKey(key);
    }

    @Override
    public boolean hasAnyRegistered() {
        return lookupTable.size() > 0;
    }

    @Override
    @Nonnull
    public Collection<BaseBlock> listAll() {
        return ObjectUtil.letNonNull(Collections.unmodifiableCollection(lookupTable.values()),
                Collections::emptySet);
    }

    @Override
    @Nonnull
    public Collection<BaseBlock> listAll(@Nullable Predicate<BaseBlock> predicate) {
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
    public Collection<NamespacedKey> listAllKeys(@Nullable Predicate<BaseBlock> predicate) {
        return predicate == null ? listAllKeys()
                : ObjectUtil.letNonNull(
                        lookupTable.entrySet().stream().filter(new Filter<>(predicate)).map(new Mapper<>())
                                .collect(Collectors.toUnmodifiableList()),
                        Collections::emptySet);
    }

    @Override
    @Nullable
    public BaseBlock findFirst(@Nullable Predicate<BaseBlock> predicate) {
        var stream = lookupTable.values().stream();
        if (predicate != null)
            stream = stream.filter(predicate);
        return stream.findFirst().orElse(null);
    }

    @Override
    @Nullable
    public NamespacedKey findFirstKey(@Nullable Predicate<BaseBlock> predicate) {
        var stream = lookupTable.entrySet().stream();
        if (predicate != null)
            stream = stream.filter(new Filter<>(predicate));
        return stream.map(new Mapper<>()).findFirst().orElse(null);
    }
}
