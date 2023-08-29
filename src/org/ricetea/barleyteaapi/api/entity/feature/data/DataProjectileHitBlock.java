package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseProjectileFeatureData;
import org.ricetea.barleyteaapi.api.block.BaseBlock;
import org.ricetea.barleyteaapi.api.block.data.DataBlockType;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataProjectileHitBlock extends BaseProjectileFeatureData<ProjectileHitEvent> {
    @Nonnull
    private final Lazy<DataBlockType> blockType;

    public DataProjectileHitBlock(@Nonnull ProjectileHitEvent event) {
        super(event);
        blockType = new Lazy<DataBlockType>(() -> BaseBlock.getBlockType(getHitBlock()));
    }

    @SuppressWarnings("null")
    @Nonnull
    public Block getHitBlock() {
        return event.getHitBlock();
    }

    @Nonnull
    public DataBlockType getHitBlockType() {
        return blockType.get();
    }

    @Nonnull
    public BlockFace getHitBlockFace() {
        BlockFace face = event.getHitBlockFace();
        return face == null ? BlockFace.SELF : face;
    }
}
