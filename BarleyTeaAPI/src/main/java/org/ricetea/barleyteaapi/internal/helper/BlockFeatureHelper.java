package org.ricetea.barleyteaapi.internal.helper;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.ricetea.barleyteaapi.api.abstracts.BaseFeatureData;
import org.ricetea.barleyteaapi.api.block.BaseBlock;
import org.ricetea.barleyteaapi.api.block.registration.BlockRegister;
import org.ricetea.utils.ObjectUtil;

public final class BlockFeatureHelper {
    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>, TFeature> boolean doFeatureCancellable(
            @Nullable Block block, @Nullable TEvent event, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull Function<TEvent, TData> dataConstructor) {
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (block != null && event != null && register != null) {
            NamespacedKey id = BaseBlock.getBlockID(block);
            if (id != null) {
                TFeature feature = ObjectUtil.tryCast(register.lookup(id), featureClass);
                if (feature != null) {
                    try {
                        boolean result = featureFunc.test(feature, dataConstructor.apply(event));
                        if (event instanceof Cancellable cancellable) {
                            result &= !cancellable.isCancelled();
                        }
                        return result;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>, TFeature, TExtraData> boolean doFeatureCancellable(
            @Nullable Block block, @Nullable TEvent event, @Nonnull TExtraData data,
            @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull BiFunction<TEvent, TExtraData, TData> dataConstructor) {
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (block != null && event != null && register != null) {
            NamespacedKey id = BaseBlock.getBlockID(block);
            if (id != null) {
                TFeature feature = ObjectUtil.tryCast(register.lookup(id), featureClass);
                if (feature != null) {
                    try {
                        boolean result = featureFunc.test(feature, dataConstructor.apply(event, data));
                        if (event instanceof Cancellable cancellable) {
                            result &= !cancellable.isCancelled();
                        }
                        return result;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>, TFeature, TReturn> TReturn doFeatureAndReturn(
            @Nullable Block block, @Nullable TEvent event,
            @Nonnull Class<TFeature> featureClass, @Nonnull BiFunction<TFeature, TData, TReturn> featureFunc,
            @Nonnull Function<TEvent, TData> dataConstructor, @Nonnull TReturn defaultValue) {
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (block != null && event != null && register != null) {
            NamespacedKey id = BaseBlock.getBlockID(block);
            if (id != null) {
                TFeature feature = ObjectUtil.tryCast(register.lookup(id), featureClass);
                if (feature != null) {
                    try {
                        TReturn result = featureFunc.apply(feature, dataConstructor.apply(event));
                        if (feature instanceof Cancellable cancellable && cancellable.isCancelled()) {
                            return defaultValue;
                        }
                        return ObjectUtil.letNonNull(result, defaultValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return defaultValue;
    }

    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>, TFeature> void doFeature(
            @Nullable Block block, @Nullable TEvent event, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiConsumer<TFeature, TData> featureFunc,
            @Nonnull Function<TEvent, TData> dataConstructor) {
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (block != null && event != null && register != null) {
            NamespacedKey id = BaseBlock.getBlockID(block);
            if (id != null) {
                try {
                    TFeature feature = ObjectUtil.tryCast(register.lookup(id), featureClass);
                    if (feature != null) {
                        featureFunc.accept(feature, dataConstructor.apply(event));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static <TFeature> void doFeature(
            @Nullable Block block, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiConsumer<TFeature, Block> featureFunc) {
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (block != null && register != null) {
            NamespacedKey id = BaseBlock.getBlockID(block);
            if (id != null) {
                try {
                    TFeature feature = ObjectUtil.tryCast(register.lookup(id), featureClass);
                    if (feature != null) {
                        featureFunc.accept(feature, block);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
