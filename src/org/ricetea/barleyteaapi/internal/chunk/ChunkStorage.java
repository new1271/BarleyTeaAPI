package org.ricetea.barleyteaapi.internal.chunk;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;
import org.ricetea.utils.ObjectUtil;

public class ChunkStorage {
    @Nullable
    public static PersistentDataContainer getBlockDataContainer(@Nonnull Block block, boolean create) {
        Chunk chunk = block.getChunk();
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        int x = block.getX() & 15;
        int y = block.getY();
        int z = block.getZ() & 15;
        NamespacedKey key = NamespacedKeyUtils.BarleyTeaAPI("block." + x + "." + y + "." + z);
        PersistentDataContainer result = container.getOrDefault(key, PersistentDataType.TAG_CONTAINER,
                null);
        if (result == null && create) {
            result = Objects.requireNonNull(container.getAdapterContext().newPersistentDataContainer());
            container.set(key, PersistentDataType.TAG_CONTAINER, result);
        }
        return result;
    }

    @Nonnull
    public static Collection<SimpleEntry<Block, PersistentDataContainer>> getBlockDataContainersFromChunk(
            @Nonnull Chunk chunk) {
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        if (!container.isEmpty()) {
            return ObjectUtil.letNonNull(container.getKeys().stream().filter(_Predicate.Instance).map(key -> {
                String coordString = key.getKey().substring(6);
                String[] coord = coordString.split("\\.");
                if (coord.length == 3) {
                    PersistentDataContainer container2 = container.get(key, PersistentDataType.TAG_CONTAINER);
                    if (container2 != null) {
                        int x = Integer.parseInt(coord[0]);
                        if (x < 0)
                            x += 16;
                        int y = Integer.parseInt(coord[1]);
                        int z = Integer.parseInt(coord[2]);
                        if (z < 0)
                            z += 16;
                        return new SimpleEntry<>(chunk.getBlock(x, y, z), container2);
                    }
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet()), Collections::emptySet);
        }
        return Objects.requireNonNull(Collections.emptySet());
    }

    public static void setBlockDataContainer(@Nonnull Block block,
            @Nullable PersistentDataContainer persistentDataContainer) {
        if (persistentDataContainer == null)
            removeBlockDataContainer(block);
        else {
            Chunk chunk = block.getChunk();
            PersistentDataContainer container = chunk.getPersistentDataContainer();
            container.set(
                    NamespacedKeyUtils.BarleyTeaAPI(
                            "block." + (block.getX() & 15) + "." + block.getY() + "." + (block.getZ() & 15)),
                    PersistentDataType.TAG_CONTAINER, persistentDataContainer);
        }
    }

    public static void removeBlockDataContainer(@Nonnull Block block) {
        Chunk chunk = block.getChunk();
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        container.remove(NamespacedKeyUtils.BarleyTeaAPI(
                "block." + (block.getX() & 15) + "." + block.getY() + "." + (block.getZ() & 15)));
    }

    public static void removeAllBlockDataContainersFromChunk(@Nonnull Chunk chunk) {
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        for (NamespacedKey key : container.getKeys().toArray(new NamespacedKey[0])) {
            if (key.getNamespace().equals(NamespacedKeyUtils.Namespace) && key.getKey().startsWith("block.")) {
                container.remove(key);
            }
        }
    }

    public static boolean hasCustomBlockData(@Nonnull Block block) {
        Chunk chunk = block.getChunk();
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        return container.has(
                NamespacedKeyUtils
                        .BarleyTeaAPI("block." + (block.getX() & 15) + "." + block.getY() + "." + (block.getZ() & 15)),
                PersistentDataType.TAG_CONTAINER);
    }

    private static class _Predicate implements Predicate<NamespacedKey> {

        public static final _Predicate Instance = new _Predicate();

        private _Predicate() {
        }

        @Override
        public boolean test(NamespacedKey key) {
            if (key == null)
                return false;
            return key.getNamespace().equals(NamespacedKeyUtils.Namespace) && key.getKey().startsWith("block.");
        }

    }
}
