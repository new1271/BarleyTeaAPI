package org.ricetea.barleyteaapi.internal.entity;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntitySpawn;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.api.entity.template.DefaultEntity;
import org.ricetea.barleyteaapi.api.internal.chunk.ChunkStorage;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtil;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BarleyFallingBlock extends DefaultEntity implements FeatureEntitySpawn {
    @Nonnull
    private static final NamespacedKey blockDataKey = NamespacedKeyUtil.BarleyTeaAPI("block_data");

    @Nonnull
    private static final Lazy<BarleyFallingBlock> _inst = Lazy.create(BarleyFallingBlock::new);

    private BarleyFallingBlock() {
        super(NamespacedKeyUtil.BarleyTeaAPI("falling_block"), EntityType.FALLING_BLOCK);
        EntityRegister.getInstance().register(this);
    }

    @Nonnull
    public static BarleyFallingBlock getInstance() {
        return _inst.get();
    }

    public static void setBlockDataContainer(@Nonnull Entity entity,
                                             @Nonnull PersistentDataContainer blockDataContainer) {
        entity.getPersistentDataContainer().set(blockDataKey, PersistentDataType.TAG_CONTAINER, blockDataContainer);
    }

    @Nullable
    public static PersistentDataContainer getBlockDataContainer(@Nonnull Entity entity) {
        return entity.getPersistentDataContainer().get(blockDataKey, PersistentDataType.TAG_CONTAINER);
    }

    @Override
    @Nonnull
    public Entity handleEntitySpawn(@Nonnull Location location) {
        Entity entity = location.getWorld().spawnEntity(location, getOriginalType());
        EntityHelper.register(this, entity);
        return entity;
    }

    @Nonnull
    public Entity handleEntitySpawn(@Nonnull Location location, @Nonnull Block block) {
        FallingBlock entity = location.getWorld().spawnFallingBlock(location, block.getBlockData());
        EntityHelper.register(this, entity);
        PersistentDataContainer blockDataContainer = ChunkStorage.getInstance().getBlockDataContainer(block, false);
        if (blockDataContainer != null && !blockDataContainer.isEmpty()) {
            entity.getPersistentDataContainer().set(blockDataKey, PersistentDataType.TAG_CONTAINER, blockDataContainer);
        }
        return entity;
    }
}
