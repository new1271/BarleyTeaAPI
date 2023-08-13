package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseProjectileFeatureData;

public final class DataProjectileHitBlock extends BaseProjectileFeatureData<ProjectileHitEvent> {
    public DataProjectileHitBlock(@Nonnull ProjectileHitEvent event) {
        super(event);
    }

    @SuppressWarnings("null")
    @Nonnull
    public Block getHitBlock() {
        return event.getHitBlock();
    }

    @Nonnull
    public BlockFace getHitBlockFace() {
        BlockFace face = event.getHitBlockFace();
        return face == null ? BlockFace.SELF : face;
    }
}
