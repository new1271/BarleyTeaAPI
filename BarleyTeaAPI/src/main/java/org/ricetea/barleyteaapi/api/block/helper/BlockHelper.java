package org.ricetea.barleyteaapi.api.block.helper;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.Unmodifiable;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.barleyteaapi.api.persistence.ExtraPersistentDataType;
import org.ricetea.barleyteaapi.internal.chunk.ChunkStorage;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Cache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class BlockHelper {

    @Nonnull
    public static final NamespacedKey DefaultNamespacedKey = NamespacedKeyUtil.BarleyTeaAPI("block_id");
    @Nonnull
    private static final Set<NamespacedKey> FallbackNamespacedKeys = ConcurrentHashMap.newKeySet();
    @Nonnull
    private static final Cache<Set<NamespacedKey>> FallbackNamespacedKeyCache =
            Cache.createInThreadSafe(() -> Collections.unmodifiableSet(FallbackNamespacedKeys));

    public static void addFallbackNamespacedKey(@Nullable NamespacedKey key) {
        if (key != null) {
            FallbackNamespacedKeys.add(key);
            FallbackNamespacedKeyCache.reset();
        }
    }

    public static void removeFallbackNamespacedKey(@Nullable NamespacedKey key) {
        if (key != null) {
            FallbackNamespacedKeys.remove(key);
            FallbackNamespacedKeyCache.reset();
        }
    }

    @Nonnull
    @Unmodifiable
    public static Set<NamespacedKey> getFallbackNamespacedKeys() {
        return FallbackNamespacedKeyCache.get();
    }

    @Nullable
    public static NamespacedKey getBlockID(@Nullable Block block) {
        if (block == null)
            return null;
        return getBlockID(getPersistentDataContainer(block, false));
    }

    @Nullable
    public static NamespacedKey getBlockID(@Nullable PersistentDataContainer container) {
        if (container == null)
            return null;
        String namespacedKeyString = container.get(DefaultNamespacedKey, PersistentDataType.STRING);
        if (namespacedKeyString == null && !FallbackNamespacedKeys.isEmpty()) {
            for (var iterator = FallbackNamespacedKeys.iterator(); iterator.hasNext() && namespacedKeyString == null; ) {
                NamespacedKey key = iterator.next();
                if (key != null)
                    namespacedKeyString = container.get(key, PersistentDataType.STRING);
            }
        }
        return namespacedKeyString == null ? null
                : namespacedKeyString.contains(":") ? NamespacedKey.fromString(namespacedKeyString) : null;
    }

    @Nullable
    public static PersistentDataContainer getPersistentDataContainer(@Nonnull Block block, boolean create) {
        return ChunkStorage.getBlockDataContainer(block, create);
    }

    public static void setPersistentDataContainer(@Nonnull Block block,
                                                  @Nullable PersistentDataContainer container) {
        ChunkStorage.setBlockDataContainer(block, container);
    }

    public static boolean isCustomBlock(@Nullable Block block) {
        return block != null && getBlockID(block) != null;
    }

    public static void register(@Nullable CustomBlock blockType, @Nullable Block block) {
        register(blockType, block, null);
    }

    public static void register(@Nullable CustomBlock blockType, @Nullable Block block,
                                @Nullable Consumer<Block> afterBlockRegistered) {
        if (blockType != null && block != null) {
            PersistentDataContainer container = Objects.requireNonNull(getPersistentDataContainer(block, true));
            container.set(DefaultNamespacedKey, ExtraPersistentDataType.NAMESPACED_KEY, blockType.getKey());
            setPersistentDataContainer(block, container);
            if (afterBlockRegistered != null) {
                afterBlockRegistered.accept(block);
            }
        }
    }

    public static boolean tryRegister(@Nullable CustomBlock blockType, @Nullable Block block,
                                      @Nullable Predicate<Block> afterBlockRegistered) {
        if (blockType != null && block != null) {
            PersistentDataContainer container = Objects.requireNonNull(getPersistentDataContainer(block, true));
            NamespacedKey previousID = container.get(DefaultNamespacedKey, ExtraPersistentDataType.NAMESPACED_KEY);
            container.set(DefaultNamespacedKey, ExtraPersistentDataType.NAMESPACED_KEY, blockType.getKey());
            if (afterBlockRegistered != null) {
                if (!afterBlockRegistered.test(block)) {
                    if (!block.isEmpty())
                        if (previousID == null) {
                            container.remove(DefaultNamespacedKey);
                            if (container.isEmpty()) {
                                ChunkStorage.removeBlockDataContainer(block);
                            }
                        } else
                            container.set(DefaultNamespacedKey, ExtraPersistentDataType.NAMESPACED_KEY, previousID);
                    return false;
                }
            }
            setPersistentDataContainer(block, container);
            return true;
        }
        return false;
    }

    public static boolean isCertainBlock(@Nullable CustomBlock blockType, @Nullable Block block) {
        if (blockType != null && block != null) {
            PersistentDataContainer container = getPersistentDataContainer(block, false);
            if (container != null) {
                return blockType.getKey().equals(getBlockID(container));
            }
        }
        return false;
    }
}
