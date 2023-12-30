package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseProjectileFeatureData;
import org.ricetea.barleyteaapi.api.block.CustomBlockType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataProjectileHitBlock extends BaseProjectileFeatureData<ProjectileHitEvent> {
    @Nonnull
    private final Lazy<CustomBlockType> blockType;

    public DataProjectileHitBlock(@Nonnull ProjectileHitEvent event) {
        super(event);
        blockType = Lazy.create(() -> CustomBlockType.get(getHitBlock()));
    }

    @Nonnull
    public Block getHitBlock() {
        return Objects.requireNonNull(event.getHitBlock());
    }

    @Nonnull
    public CustomBlockType getHitBlockType() {
        return blockType.get();
    }

    @Nonnull
    public BlockFace getHitBlockFace() {
        BlockFace face = event.getHitBlockFace();
        return face == null ? BlockFace.SELF : face;
    }
}
