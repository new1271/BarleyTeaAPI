package org.ricetea.barleyteaapi.internal.linker;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.base.data.BaseFeatureData;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockLoad;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

@ApiStatus.Internal
public final class BlockFeatureLinker {
    @Nonnull
    private static final Set<Block> loadedBlocks = Collections.newSetFromMap(new WeakHashMap<>());

    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>, TFeature> boolean doFeatureCancellable(
            @Nullable Block block, @Nullable TEvent event, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull Function<TEvent, TData> dataConstructor) {
        if (event == null)
            return true;
        TFeature feature = ObjectUtil.tryCast(CustomBlock.get(block), featureClass);
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

    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>, TFeature, TExtraData> boolean doFeatureCancellable(
            @Nullable Block block, @Nullable TEvent event, @Nonnull TExtraData data,
            @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull BiFunction<TEvent, TExtraData, TData> dataConstructor) {
        if (event == null)
            return true;
        TFeature feature = ObjectUtil.tryCast(CustomBlock.get(block), featureClass);
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
    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>, TFeature, TReturn> TReturn doFeatureAndReturn(
            @Nullable Block block, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiFunction<TFeature, TData, TReturn> featureFunc,
            @Nonnull Function<TEvent, TData> dataConstructor, @Nonnull TReturn defaultValue) {
        if (event == null)
            return defaultValue;
        TFeature feature = ObjectUtil.tryCast(CustomBlock.get(block), featureClass);
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

    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>, TFeature> void doFeature(
            @Nullable Block block, @Nullable TEvent event, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiConsumer<TFeature, TData> featureFunc,
            @Nonnull Function<TEvent, TData> dataConstructor) {
        if (event == null)
            return;
        TFeature feature = ObjectUtil.tryCast(CustomBlock.get(block), featureClass);
        if (feature == null)
            return;
        ObjectUtil.tryCall(() -> featureFunc.accept(feature, dataConstructor.apply(event)));
    }

    public static <TFeature> void doFeature(
            @Nullable Block block, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiConsumer<TFeature, Block> featureFunc) {
        TFeature feature = ObjectUtil.tryCast(CustomBlock.get(block), featureClass);
        if (feature == null)
            return;
        ObjectUtil.tryCall(() -> featureFunc.accept(feature, block));
    }

    public static void loadBlock(@Nonnull Block block) {
        CustomBlock blockType = CustomBlock.get(block);
        if (blockType == null)
            return;
        loadBlock(block);
    }

    public static void loadBlock(@Nonnull CustomBlock blockType, @Nonnull Block block) {
        if (blockType instanceof FeatureBlockLoad feature) {
            loadBlock(feature, block);
        }
    }

    public static void loadBlock(@Nonnull FeatureBlockLoad feature, @Nonnull Block block) {
        if (block.isEmpty())
            return;
        synchronized (loadedBlocks) {
            if (!loadedBlocks.add(block))
                return;
        }
        ObjectUtil.tryCall(() -> feature.handleBlockLoaded(block));
    }

    public static void unloadBlock(@Nonnull Block block) {
        CustomBlock blockType = CustomBlock.get(block);
        if (blockType == null)
            return;
        unloadBlock(blockType, block);
    }

    public static void unloadBlock(@Nonnull CustomBlock blockType, @Nonnull Block block) {
        if (blockType instanceof FeatureBlockLoad feature) {
            unloadBlock(feature, block);
        }
    }

    public static void unloadBlock(@Nonnull FeatureBlockLoad feature, @Nonnull Block block) {
        synchronized (loadedBlocks) {
            if (!loadedBlocks.remove(block))
                return;
        }
        ObjectUtil.tryCall(() -> feature.handleBlockUnloaded(block));
    }
}
