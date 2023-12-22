package org.ricetea.barleyteaapi.api.block.registration;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.abstracts.IRegister;
import org.ricetea.barleyteaapi.api.block.BaseBlock;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockLoad;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockTick;
import org.ricetea.barleyteaapi.api.event.BlocksRegisteredEvent;
import org.ricetea.barleyteaapi.api.event.BlocksUnregisteredEvent;
import org.ricetea.barleyteaapi.internal.chunk.ChunkStorage;
import org.ricetea.barleyteaapi.internal.task.BlockTickTask;
import org.ricetea.utils.CollectionUtil;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class BlockRegister implements IRegister<BaseBlock> {
    @Nonnull
    private static final Lazy<BlockRegister> inst = Lazy.create(BlockRegister::new);

    @Nonnull
    private final ConcurrentHashMap<NamespacedKey, BaseBlock> lookupTable = new ConcurrentHashMap<>();

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

    @Override
    public void registerAll(@Nullable Collection<BaseBlock> blocks) {
        if (blocks == null)
            return;
        refreshCustomBlocks(
                blocks.stream()
                        .map(block -> RefreshCustomBlockRecord.create(lookupTable.put(block.getKey(), block), block))
                        .filter(Objects::nonNull)
                        .toList());
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                Logger logger = inst.getLogger();
                for (BaseBlock block : blocks)
                    logger.info("registered " + block.getKey() + " as block!");
                Bukkit.getPluginManager().callEvent(new BlocksRegisteredEvent(blocks));
            }
        }
    }

    @Override
    public void register(@Nullable BaseBlock block) {
        if (block == null)
            return;
        var record = RefreshCustomBlockRecord.create(lookupTable.put(block.getKey(), block), block);
        if (record != null)
            refreshCustomBlocks(List.of(record));
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                Logger logger = inst.getLogger();
                logger.info("registered " + block.getKey() + " as block!");
                Bukkit.getPluginManager().callEvent(new BlocksRegisteredEvent(List.of(block)));
            }
        }
    }

    @Override
    public void unregister(@Nullable BaseBlock block) {
        if (block == null || !lookupTable.remove(block.getKey(), block))
            return;
        var record = RefreshCustomBlockRecord.create(block, null);
        if (record != null)
            refreshCustomBlocks(List.of(record));
        BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
        if (inst != null) {
            Logger logger = inst.getLogger();
            logger.info("unregistered " + block.getKey());
            Bukkit.getPluginManager().callEvent(new BlocksUnregisteredEvent(List.of(block)));
        }
    }

    @Override
    public void unregisterAll() {
        var keySet = Collections.unmodifiableSet(lookupTable.keySet());
        var values = CollectionUtil.toUnmodifiableList(lookupTable.values());
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                if (!chunk.isGenerated())
                    continue;
                for (var entry : ChunkStorage.getBlockDataContainersFromChunk(chunk)) {
                    Block iteratedBlock = entry.getKey();
                    NamespacedKey key = BaseBlock.getBlockID(entry.getValue());
                    if (key == null)
                        continue;
                    BaseBlock block = lookupTable.get(key);
                    if (block instanceof FeatureBlockLoad feature)
                        feature.handleBlockUnloaded(iteratedBlock);
                    if (block instanceof FeatureBlockTick) {
                        BlockTickTask task = BlockTickTask.getInstanceUnsafe();
                        if (task != null) {
                            task.removeBlock(iteratedBlock);
                        }
                    }
                }
            }
        }
        lookupTable.clear();
        Logger logger = ObjectUtil.safeMap(BarleyTeaAPI.getInstanceUnsafe(), BarleyTeaAPI::getLogger);
        if (logger != null) {
            for (NamespacedKey key : keySet) {
                logger.info("unregistered " + key.getKey());
            }
        }
        Bukkit.getPluginManager().callEvent(new BlocksUnregisteredEvent(values));
    }

    @Override
    public void unregisterAll(@Nullable Predicate<BaseBlock> predicate) {
        if (predicate == null)
            unregisterAll();
        else {
            ArrayList<RefreshCustomBlockRecord> collectingList = new ArrayList<>();
            ArrayList<BaseBlock> collectingList2 = new ArrayList<>();
            Logger logger = null;
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                logger = inst.getLogger();
            }
            for (var iterator = lookupTable.entrySet().iterator(); iterator.hasNext(); ) {
                var entry = iterator.next();
                NamespacedKey key = entry.getKey();
                BaseBlock blockType = entry.getValue();
                if (predicate.test(blockType)) {
                    iterator.remove();
                    var record = RefreshCustomBlockRecord.create(blockType, null);
                    if (record != null)
                        collectingList.add(record);
                    collectingList2.add(blockType);
                    if (logger != null)
                        logger.info("unregistered " + key);
                }
            }
            refreshCustomBlocks(collectingList);
            Bukkit.getPluginManager().callEvent(new BlocksUnregisteredEvent(collectingList2));
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
        return !lookupTable.isEmpty();
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
    public Collection<NamespacedKey> listAllKeys(@Nullable Predicate<BaseBlock> predicate) {
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

    private record RefreshCustomBlockRecord(@Nullable NamespacedKey key,
                                            @Nullable FeatureBlockLoad oldFeature,
                                            @Nullable FeatureBlockLoad newFeature,
                                            boolean hasTickingOld, boolean hasTickingNew) {

        @Nullable
        public static RefreshCustomBlockRecord create(@Nullable BaseBlock oldBlock, @Nullable BaseBlock newBlock) {
            BaseBlock compareBlock = newBlock == null ? oldBlock : newBlock;
            if (compareBlock == null)
                return null;
            return new RefreshCustomBlockRecord(compareBlock.getKey(),
                    ObjectUtil.tryCast(oldBlock, FeatureBlockLoad.class),
                    ObjectUtil.tryCast(newBlock, FeatureBlockLoad.class),
                    oldBlock instanceof FeatureBlockTick,
                    newBlock instanceof FeatureBlockTick);
        }

        public boolean needOperate() {
            return hasTickingOld || hasTickingNew || oldFeature != null || newFeature != null;
        }
    }

    private void refreshCustomBlocks(@Nonnull Collection<RefreshCustomBlockRecord> records) {
        if (records.stream().anyMatch(RefreshCustomBlockRecord::needOperate)) {
            for (World world : Bukkit.getWorlds()) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    if (!chunk.isGenerated())
                        continue;
                    for (var entry : ChunkStorage.getBlockDataContainersFromChunk(chunk)) {
                        Block block = entry.getKey();
                        NamespacedKey key = BaseBlock.getBlockID(entry.getValue());
                        if (key == null)
                            continue;
                        records.stream()
                                .filter(record -> key.equals(record.key()))
                                .findAny()
                                .ifPresent(record -> {
                                    BarleyTeaAPI plugin = BarleyTeaAPI.getInstanceUnsafe();
                                    if (plugin != null) {
                                        BukkitScheduler scheduler = Bukkit.getScheduler();
                                        FeatureBlockLoad feature = record.oldFeature();
                                        if (feature != null) {
                                            final FeatureBlockLoad finalFeature = feature;
                                            scheduler.scheduleSyncDelayedTask(plugin,
                                                    () -> finalFeature.handleBlockUnloaded(block));
                                        }
                                        feature = record.newFeature;
                                        if (feature != null) {
                                            final FeatureBlockLoad finalFeature = feature;
                                            scheduler.scheduleSyncDelayedTask(plugin,
                                                    () -> finalFeature.handleBlockLoaded(block));
                                        }
                                    }
                                    boolean hasTickingOld = record.hasTickingOld();
                                    boolean hasTickingNew = record.hasTickingNew();
                                    if (hasTickingOld != hasTickingNew) {
                                        if (hasTickingOld) {
                                            BlockTickTask task = BlockTickTask.getInstanceUnsafe();
                                            if (task != null) {
                                                task.removeBlock(block);
                                            }
                                        } else {
                                            BlockTickTask.getInstance().addBlock(block);
                                        }
                                    }
                                });
                    }
                }
            }
        }
    }
}
