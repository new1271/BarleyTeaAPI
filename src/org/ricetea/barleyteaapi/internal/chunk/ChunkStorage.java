package org.ricetea.barleyteaapi.internal.chunk;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;

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
}
