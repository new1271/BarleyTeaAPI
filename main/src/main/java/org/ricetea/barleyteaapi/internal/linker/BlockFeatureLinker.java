package org.ricetea.barleyteaapi.internal.linker;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.base.data.BaseFeatureData;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

@ApiStatus.Internal
public final class BlockFeatureLinker {
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
}
