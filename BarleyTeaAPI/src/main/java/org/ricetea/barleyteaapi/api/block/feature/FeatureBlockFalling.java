package org.ricetea.barleyteaapi.api.block.feature;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.persistence.PersistentDataContainer;

public interface FeatureBlockFalling {
    boolean handleBlockStartFall(@Nonnull Block block);

    boolean handleBlockFallToGround(@Nonnull Block block);

    boolean handleBlockFallDropItem(@Nonnull PersistentDataContainer blockDataContainer,
            @Nonnull Item dropItemEntity);
}
