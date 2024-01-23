package org.ricetea.barleyteaapi.internal.block.registration;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockLoad;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockTick;
import org.ricetea.barleyteaapi.api.block.helper.BlockHelper;
import org.ricetea.barleyteaapi.api.block.registration.BlockRegister;
import org.ricetea.barleyteaapi.api.event.BlocksRegisteredEvent;
import org.ricetea.barleyteaapi.api.event.BlocksUnregisteredEvent;
import org.ricetea.barleyteaapi.api.localization.LocalizationRegister;
import org.ricetea.barleyteaapi.api.localization.LocalizedMessageFormat;
import org.ricetea.barleyteaapi.internal.base.registration.NSKeyedRegisterBase;
import org.ricetea.barleyteaapi.internal.chunk.ChunkStorage;
import org.ricetea.barleyteaapi.internal.task.BlockTickTask;
import org.ricetea.barleyteaapi.util.SyncUtil;
import org.ricetea.utils.Constants;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
@ApiStatus.Internal
public final class BlockRegisterImpl extends NSKeyedRegisterBase<CustomBlock> implements BlockRegister {
    @Nonnull
    private static final Lazy<BlockRegisterImpl> inst = Lazy.create(BlockRegisterImpl::new);

    private BlockRegisterImpl() {
    }

    @Nonnull
    public static BlockRegisterImpl getInstance() {
        return inst.get();
    }

    @Nullable
    public static BlockRegisterImpl getInstanceUnsafe() {
        return inst.getUnsafe();
    }

    @Override
    public void register(@Nullable CustomBlock block) {
        if (block == null)
            return;
        registerAll(Set.of(block));
    }

    @Override
    public void registerAll(@Nullable Collection<CustomBlock> blocks) {
        if (blocks == null)
            return;
        LocalizationRegister localizationRegister = LocalizationRegister.getInstance();
        refreshCustomBlocks(
                blocks.stream()
                        .map(_block -> RefreshCustomBlockRecord.create(getLookupMap().put(_block.getKey(), _block),
                                _block))
                        .filter(Objects::nonNull)
                        .toList());
        blocks.forEach(_block -> {
            if (_block == null)
                return;
            LocalizedMessageFormat format = LocalizedMessageFormat.create(_block.getTranslationKey());
            format.setFormat(new MessageFormat(_block.getDefaultName()));
            localizationRegister.register(format);
        });
        BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
        if (inst != null) {
            Logger logger = inst.getLogger();
            blocks.forEach(_block -> {
                if (_block == null)
                    return;
                logger.info(LOGGING_REGISTERED_FORMAT.formatted(_block.getKey(), "block"));
            });
        }
        SyncUtil.callInMainThread(inst,
                () -> Bukkit.getPluginManager().callEvent(new BlocksRegisteredEvent(blocks)),
                false);
    }

    @Override
    public void unregister(@Nullable CustomBlock block) {
        if (block == null || !getLookupMap().remove(block.getKey(), block))
            return;
        Set<CustomBlock> blocks = Set.of(block);
        refreshCustomBlocks(blocks.stream()
                .map(_block -> RefreshCustomBlockRecord.create(_block, null))
                .toList());
        BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
        if (inst != null) {
            Logger logger = inst.getLogger();
            blocks.forEach(item ->
                    logger.info(LOGGING_UNREGISTERED_FORMAT.formatted(item.getKey())));
        }
        SyncUtil.callInMainThread(inst,
                () -> Bukkit.getPluginManager().callEvent(new BlocksUnregisteredEvent(blocks)),
                false);
    }

    @Override
    public void unregisterAll(@Nullable Predicate<CustomBlock> predicate) {
        if (isEmpty())
            return;
        Map<NamespacedKey, CustomBlock> lookupMap = getLookupMap();
        Collection<CustomBlock> values = lookupMap.values();
        Stream<CustomBlock> stream = values.stream();
        if (predicate != null) {
            if (getCachedSize() >= Constants.MIN_ITERATION_COUNT_FOR_PARALLEL)
                stream = stream.parallel();
            stream = stream.filter(predicate);
        }
        Set<CustomBlock> blocks = stream.collect(Collectors.toUnmodifiableSet());
        if (blocks.isEmpty())
            return;
        values.removeAll(blocks);
        refreshCustomBlocks(blocks.stream()
                .map(_block -> RefreshCustomBlockRecord.create(_block, null))
                .toList());
        BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
        if (inst != null) {
            Logger logger = inst.getLogger();
            blocks.forEach(item ->
                    logger.info(LOGGING_UNREGISTERED_FORMAT.formatted(item.getKey())));
        }
        SyncUtil.callInMainThread(inst,
                () -> Bukkit.getPluginManager().callEvent(new BlocksUnregisteredEvent(blocks)),
                false);
    }

    private void refreshCustomBlocks(@Nonnull Collection<RefreshCustomBlockRecord> records) {
        if (records.stream().anyMatch(RefreshCustomBlockRecord::needOperate)) {
            for (World world : Bukkit.getWorlds()) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    if (!chunk.isGenerated())
                        continue;
                    for (var entry : ChunkStorage.getBlockDataContainersFromChunk(chunk)) {
                        Block block = entry.getKey();
                        NamespacedKey key = BlockHelper.getBlockID(entry.getValue());
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
        refreshCachedSize();
    }

    private record RefreshCustomBlockRecord(@Nullable NamespacedKey key,
                                            @Nullable FeatureBlockLoad oldFeature,
                                            @Nullable FeatureBlockLoad newFeature,
                                            boolean hasTickingOld, boolean hasTickingNew) {

        @Nullable
        public static RefreshCustomBlockRecord create(@Nullable CustomBlock oldBlock, @Nullable CustomBlock newBlock) {
            CustomBlock compareBlock = newBlock == null ? oldBlock : newBlock;
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
}
