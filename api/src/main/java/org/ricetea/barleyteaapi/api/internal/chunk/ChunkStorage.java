package org.ricetea.barleyteaapi.api.internal.chunk;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Objects;

public interface ChunkStorage {

    @Nonnull
    static ChunkStorage getInstance() {
        return Objects.requireNonNull(getInstanceUnsafe());
    }

    @Nullable
    static ChunkStorage getInstanceUnsafe() {
        return Bukkit.getServicesManager().load(ChunkStorage.class);
    }

    @Nullable
    PersistentDataContainer getBlockDataContainer(@Nonnull Block block, boolean create);

    @Nonnull
    Collection<AbstractMap.SimpleImmutableEntry<Block, PersistentDataContainer>> getBlockDataContainersFromChunk
            (@Nonnull Chunk chunk);

    void setBlockDataContainer(@Nonnull Block block, @Nullable PersistentDataContainer container);

    void removeBlockDataContainer(@Nonnull Block block);

    void removeAllBlockDataContainersFromChunk(@Nonnull Chunk chunk);

    boolean hasCustomBlockData(@Nonnull Block block);
}
