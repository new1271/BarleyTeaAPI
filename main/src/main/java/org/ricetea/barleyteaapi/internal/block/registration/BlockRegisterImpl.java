package org.ricetea.barleyteaapi.internal.block.registration;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.barleyteaapi.api.block.feature.BlockFeature;
import org.ricetea.barleyteaapi.api.block.helper.BlockHelper;
import org.ricetea.barleyteaapi.api.block.registration.BlockRegister;
import org.ricetea.barleyteaapi.api.event.BlocksRegisteredEvent;
import org.ricetea.barleyteaapi.api.event.BlocksUnregisteredEvent;
import org.ricetea.barleyteaapi.api.internal.chunk.ChunkStorage;
import org.ricetea.barleyteaapi.api.localization.LocalizationRegister;
import org.ricetea.barleyteaapi.api.localization.LocalizedMessageFormat;
import org.ricetea.barleyteaapi.internal.base.registration.CustomObjectRegisterBase;
import org.ricetea.barleyteaapi.internal.linker.BlockFeatureLinker;
import org.ricetea.barleyteaapi.internal.linker.BlockFeatureLinker.RefreshCustomBlockRecord;
import org.ricetea.barleyteaapi.util.SyncUtil;
import org.ricetea.utils.Constants;

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
public final class BlockRegisterImpl extends CustomObjectRegisterBase<CustomBlock, BlockFeature> implements BlockRegister {

    @Override
    public void register(@Nullable CustomBlock block) {
        if (block == null)
            return;
        registerAll(Set.of(block));
    }

    @Override
    public void registerAll(@Nullable Collection<? extends CustomBlock> blocks) {
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
            String translationKey = _block.getTranslationKey();
            LocalizedMessageFormat oldFormat = localizationRegister.lookup(translationKey);
            if (oldFormat != null && oldFormat.getLocales().contains(LocalizedMessageFormat.DEFAULT_LOCALE))
                return;
            LocalizedMessageFormat format = LocalizedMessageFormat.create(translationKey);
            if (oldFormat != null) {
                oldFormat.getLocales().forEach(locale ->
                        format.setFormat(locale, oldFormat.getFormat(locale)));
            }
            format.setFormat(new MessageFormat(_block.getDefaultName()));
            localizationRegister.register(format);
            registerFeatures(_block);
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
        unregisterFeatures(block);
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
    public void unregisterAll(@Nullable Predicate<? super CustomBlock> predicate) {
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
        blocks.forEach(this::unregisterFeatures);
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
            ChunkStorage chunkStorage = ChunkStorage.getInstance();
            for (World world : Bukkit.getWorlds()) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    if (!chunk.isGenerated())
                        continue;
                    for (var entry : chunkStorage.getBlockDataContainersFromChunk(chunk)) {
                        Block block = entry.getKey();
                        NamespacedKey key = BlockHelper.getBlockID(entry.getValue());
                        if (key == null)
                            continue;
                        records.stream()
                                .filter(record -> key.equals(record.key()))
                                .findAny()
                                .ifPresent(record -> BlockFeatureLinker.refreshBlock(block, record));
                    }
                }
            }
        }
        refreshCachedSize();
    }
}
