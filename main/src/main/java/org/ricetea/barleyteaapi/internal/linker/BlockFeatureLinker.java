package org.ricetea.barleyteaapi.internal.linker;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.base.data.BaseFeatureData;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.barleyteaapi.api.block.feature.BlockFeature;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockLoad;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockTick;
import org.ricetea.barleyteaapi.internal.task.BlockTickTask;
import org.ricetea.barleyteaapi.util.SyncUtil;
import org.ricetea.utils.BlockLocation;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

@ApiStatus.Internal
public final class BlockFeatureLinker {

    @Nonnull
    private static final Map<BlockLocation, LoadBlockData> loadedBlocks = new ConcurrentHashMap<>();

    private record LoadBlockData(@Nullable FeatureBlockLoad loadFeature, boolean ticking) {
    }

    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>, TFeature extends BlockFeature> boolean doFeatureCancellable(
            @Nullable Block block, @Nullable TEvent event, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull Function<TEvent, TData> dataConstructor) {
        if (event == null)
            return true;
        TFeature feature = ObjectUtil.tryMap(CustomBlock.get(block), obj -> obj.getFeature(featureClass));
        if (feature == null)
            return true;
        return ObjectUtil.tryMap(() -> {
            boolean result = featureFunc.test(feature, dataConstructor.apply(event));
            if (event instanceof Cancellable cancellable) {
                result &= !cancellable.isCancelled();
            }
            return result;
        }, true);
    }

    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>, TFeature extends BlockFeature, TExtraData> boolean doFeatureCancellable(
            @Nullable Block block, @Nullable TEvent event, @Nonnull TExtraData data,
            @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull BiFunction<TEvent, TExtraData, TData> dataConstructor) {
        if (event == null)
            return true;
        TFeature feature = ObjectUtil.tryMap(CustomBlock.get(block), obj -> obj.getFeature(featureClass));
        if (feature == null)
            return true;
        return ObjectUtil.tryMap(() -> {
            boolean result = featureFunc.test(feature, dataConstructor.apply(event, data));
            if (event instanceof Cancellable cancellable) {
                result &= !cancellable.isCancelled();
            }
            return result;
        }, true);
    }

    @Nonnull
    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>, TFeature extends BlockFeature, TReturn> TReturn doFeatureAndReturn(
            @Nullable Block block, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiFunction<TFeature, TData, TReturn> featureFunc,
            @Nonnull Function<TEvent, TData> dataConstructor, @Nonnull TReturn defaultValue) {
        if (event == null)
            return defaultValue;
        TFeature feature = ObjectUtil.tryMap(CustomBlock.get(block), obj -> obj.getFeature(featureClass));
        if (feature == null)
            return defaultValue;
        return ObjectUtil.tryMap(() -> {
            TReturn result = featureFunc.apply(feature, dataConstructor.apply(event));
            if (feature instanceof Cancellable cancellable && cancellable.isCancelled()) {
                return null;
            }
            return result;
        }, defaultValue);
    }

    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>, TFeature extends BlockFeature> void doFeature(
            @Nullable Block block, @Nullable TEvent event, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiConsumer<TFeature, TData> featureFunc,
            @Nonnull Function<TEvent, TData> dataConstructor) {
        if (event == null)
            return;
        TFeature feature = ObjectUtil.tryMap(CustomBlock.get(block), obj -> obj.getFeature(featureClass));
        if (feature == null)
            return;
        ObjectUtil.tryCall(() -> featureFunc.accept(feature, dataConstructor.apply(event)));
    }

    public static <TFeature extends BlockFeature> void doFeature(
            @Nullable Block block, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiConsumer<TFeature, Block> featureFunc) {
        TFeature feature = ObjectUtil.tryMap(CustomBlock.get(block), obj -> obj.getFeature(featureClass));
        if (feature == null)
            return;
        ObjectUtil.tryCall(() -> featureFunc.accept(feature, block));
    }

    public static void loadBlock(@Nonnull Block block, boolean loadOnly) {
        CustomBlock blockType = CustomBlock.get(block);
        if (blockType == null)
            return;
        loadBlock(blockType, block, loadOnly);
    }

    public static void loadBlock(@Nonnull CustomBlock blockType, @Nonnull Block block, boolean loadOnly) {
        if (block.isEmpty())
            return;
        BlockLocation location = new BlockLocation(block);
        FeatureBlockLoad feature = blockType.getFeature(FeatureBlockLoad.class);
        boolean needTick = !loadOnly && blockType.getFeature(FeatureBlockTick.class) != null;
        LoadBlockData newBlockData = new LoadBlockData(feature, needTick);
        LoadBlockData oldBlockData = loadedBlocks.put(location, newBlockData);
        if (oldBlockData != null) {
            ObjectUtil.tryCall(oldBlockData.loadFeature, _feature ->
                    SyncUtil.callInMainThread(() -> _feature.handleBlockUnloaded(block)));
            if (oldBlockData.ticking && !needTick) {
                ObjectUtil.safeCall(BlockTickTask.getInstanceUnsafe(), task -> task.removeBlock(block));
            }
        }
        if (feature == null && !needTick)
            return;
        ObjectUtil.tryCall(feature, _feature ->
                SyncUtil.callInMainThread(() -> _feature.handleBlockLoaded(block)));
        if (needTick)
            BlockTickTask.getInstance().addBlock(block);
    }

    public static void unloadBlock(@Nonnull Block block) {
        CustomBlock blockType = CustomBlock.get(block);
        if (blockType == null)
            return;
        unloadBlock(blockType, block);
    }

    public static void unloadBlock(@Nonnull CustomBlock blockType, @Nonnull Block block) {
        BlockLocation location = new BlockLocation(block);
        loadedBlocks.computeIfPresent(location, (loc, oldBlockData) -> {
            LoadBlockData result = oldBlockData;
            FeatureBlockLoad oldFeature = oldBlockData.loadFeature;
            if (Objects.equals(ObjectUtil.tryCast(oldFeature, CustomBlock.class), blockType)) {
                ObjectUtil.tryCall(oldFeature, _feature ->
                        SyncUtil.callInMainThread(() -> _feature.handleBlockUnloaded(block)));
                result = null;
            }
            if (oldBlockData.ticking) {
                ObjectUtil.safeCall(BlockTickTask.getInstanceUnsafe(), task -> task.removeBlock(block));
            }
            return result;
        });
    }

    public static void moveBlock(@Nonnull CustomBlock blockType, @Nonnull Block blockFrom, @Nonnull Block blockTo) {
        unloadBlock(blockType, blockFrom);
        loadBlock(blockType, blockTo, false);
    }

    public static void refreshBlock(@Nonnull Block block, @Nonnull RefreshCustomBlockRecord record) {
        BlockLocation location = new BlockLocation(block);
        loadedBlocks.compute(location, (loc, oldBlockData) -> {
            if (oldBlockData != null) {
                ObjectUtil.tryCall(oldBlockData.loadFeature, _feature ->
                        SyncUtil.callInMainThread(() -> _feature.handleBlockUnloaded(block)));
                if (oldBlockData.ticking && !record.hasTickingNew) {
                    ObjectUtil.safeCall(BlockTickTask.getInstanceUnsafe(),
                            task -> task.removeBlock(block));
                }
            }
            ObjectUtil.tryCall(record.newLoadFeature, _feature ->
                    SyncUtil.callInMainThread(() -> _feature.handleBlockLoaded(block)));
            if (record.hasTickingNew)
                BlockTickTask.getInstance().addBlock(block);
            return new LoadBlockData(record.newLoadFeature, record.hasTickingNew);
        });
    }


    public record RefreshCustomBlockRecord(@Nullable NamespacedKey key,
                                           @Nullable FeatureBlockLoad oldLoadFeature,
                                           @Nullable FeatureBlockLoad newLoadFeature,
                                           boolean hasTickingOld, boolean hasTickingNew) {

        @Nullable
        public static RefreshCustomBlockRecord create(@Nullable CustomBlock oldBlock, @Nullable CustomBlock newBlock) {
            CustomBlock compareBlock = newBlock == null ? oldBlock : newBlock;
            if (compareBlock == null)
                return null;
            return new RefreshCustomBlockRecord(compareBlock.getKey(),
                    ObjectUtil.tryMap(oldBlock, _block -> _block.getFeature(FeatureBlockLoad.class)),
                    ObjectUtil.tryMap(newBlock, _block -> _block.getFeature(FeatureBlockLoad.class)),
                    ObjectUtil.tryMap(
                            newBlock,
                            _block -> _block.getFeature(FeatureBlockTick.class) != null,
                            false
                    ),
                    ObjectUtil.tryMap(
                            newBlock,
                            _block -> _block.getFeature(FeatureBlockTick.class) != null,
                            false
                    ));
        }

        public boolean needOperate() {
            return hasTickingOld || hasTickingNew || oldLoadFeature != null || newLoadFeature != null;
        }
    }
}
