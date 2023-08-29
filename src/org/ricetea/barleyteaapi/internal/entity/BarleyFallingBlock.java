package org.ricetea.barleyteaapi.internal.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntitySpawn;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.internal.chunk.ChunkStorage;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;

public final class BarleyFallingBlock extends BaseEntity implements FeatureEntitySpawn {
    @Nonnull
    private static final NamespacedKey blockDataKey = NamespacedKeyUtils.BarleyTeaAPI("block_data");

    @Nonnull
    private static final Lazy<BarleyFallingBlock> _inst = new Lazy<>(BarleyFallingBlock::new);

    private BarleyFallingBlock() {
        super(NamespacedKeyUtils.BarleyTeaAPI("falling_block"), EntityType.FALLING_BLOCK);
        EntityRegister.getInstance().register(this);
    }

    @Nonnull
    public static BarleyFallingBlock getInstance() {
        return _inst.get();
    }

    @Override
    @Nullable
    public Entity handleEntitySpawn(@Nonnull Location location) {
        Entity entity = location.getWorld().spawnEntity(location, getEntityTypeBasedOn());
        register(entity);
        return entity;
    }

    @Nullable
    public Entity handleEntitySpawn(@Nonnull Location location, @Nonnull Block block) {
        FallingBlock entity = location.getWorld().spawnFallingBlock(location, block.getBlockData());
        register(entity);
        PersistentDataContainer blockDataContainer = ChunkStorage.getBlockDataContainer(block, false);
        if (blockDataContainer != null && !blockDataContainer.isEmpty()) {
            entity.getPersistentDataContainer().set(blockDataKey, PersistentDataType.TAG_CONTAINER, blockDataContainer);
        }
        return entity;
    }

    public static void setBlockDataContainer(@Nonnull Entity entity,
            @Nonnull PersistentDataContainer blockDataContainer) {
        entity.getPersistentDataContainer().set(blockDataKey, PersistentDataType.TAG_CONTAINER, blockDataContainer);
    }

    @Nullable
    public static PersistentDataContainer getBlockDataContainer(@Nonnull Entity entity) {
        return entity.getPersistentDataContainer().get(blockDataKey, PersistentDataType.TAG_CONTAINER);
    }
}
