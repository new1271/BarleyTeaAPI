package org.ricetea.barleyteaapi.internal.chunk;

import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.internal.chunk.ChunkStorage;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@ApiStatus.Internal
public final class ChunkStorageImpl implements ChunkStorage {
    @Nullable
    public PersistentDataContainer getBlockDataContainer(@Nonnull Block block, boolean create) {
        Chunk chunk = block.getChunk();
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        int x = block.getX() & 15;
        int y = block.getY();
        int z = block.getZ() & 15;
        NamespacedKey key = NamespacedKeyUtil.BarleyTeaAPI("block." + x + "." + y + "." + z);
        PersistentDataContainer result = container.get(key, PersistentDataType.TAG_CONTAINER);
        if (result == null && create) {
            result = Objects.requireNonNull(container.getAdapterContext().newPersistentDataContainer());
            container.set(key, PersistentDataType.TAG_CONTAINER, result);
        }
        return result;
    }

    @Nonnull
    public Collection<SimpleImmutableEntry<Block, PersistentDataContainer>> getBlockDataContainersFromChunk(
            @Nonnull Chunk chunk) {
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        if (!container.isEmpty()) {
            return ObjectUtil.letNonNull(container.getKeys().stream()
                    .filter(_Predicate.Instance)
                    .map(key -> {
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
                                return new SimpleImmutableEntry<>(chunk.getBlock(x, y, z), container2);
                            }
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableSet()), Collections::emptySet);
        }
        return Objects.requireNonNull(Collections.emptySet());
    }

    public void setBlockDataContainer(@Nonnull Block block, @Nullable PersistentDataContainer container) {
        if (container == null)
            removeBlockDataContainer(block);
        else {
            Chunk chunk = block.getChunk();
            PersistentDataContainer chunkContainer = chunk.getPersistentDataContainer();
            chunkContainer.set(
                    NamespacedKeyUtil.BarleyTeaAPI(
                            "block." + (block.getX() & 15) + "." + block.getY() + "." + (block.getZ() & 15)),
                    PersistentDataType.TAG_CONTAINER, container);
        }
    }

    public void removeBlockDataContainer(@Nonnull Block block) {
        Chunk chunk = block.getChunk();
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        container.remove(NamespacedKeyUtil.BarleyTeaAPI(
                "block." + (block.getX() & 15) + "." + block.getY() + "." + (block.getZ() & 15)));
    }

    public void removeAllBlockDataContainersFromChunk(@Nonnull Chunk chunk) {
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        for (NamespacedKey key : container.getKeys().toArray(NamespacedKey[]::new)) {
            if (key.getNamespace().equals(NamespacedKeyUtil.BarleyTeaAPI) && key.getKey().startsWith("block.")) {
                container.remove(key);
            }
        }
    }

    public boolean hasCustomBlockData(@Nonnull Block block) {
        Chunk chunk = block.getChunk();
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        return container.has(
                NamespacedKeyUtil
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
            return key.getNamespace().equals(NamespacedKeyUtil.BarleyTeaAPI) && key.getKey().startsWith("block.");
        }

    }
}
