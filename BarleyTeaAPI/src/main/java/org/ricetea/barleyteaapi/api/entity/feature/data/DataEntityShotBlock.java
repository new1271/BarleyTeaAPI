package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityShotFeatureData;
import org.ricetea.barleyteaapi.api.block.BaseBlock;
import org.ricetea.barleyteaapi.api.block.data.DataBlockType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataEntityShotBlock extends BaseEntityShotFeatureData {
    @Nonnull
    private final Lazy<DataBlockType> blockType;

    public DataEntityShotBlock(@Nonnull ProjectileHitEvent event) {
        super(event);
        blockType = Lazy.create(() -> BaseBlock.getBlockType(getHitBlock()));
    }

    @Nonnull
    public Block getHitBlock() {
        return Objects.requireNonNull(event.getHitBlock());
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
