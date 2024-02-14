package org.ricetea.barleyteaapi.api.block.feature;

import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.persistence.PersistentDataContainer;

import javax.annotation.Nonnull;

public interface FeatureBlockFalling extends BlockFeature {
    boolean handleBlockStartFall(@Nonnull Block block);

    boolean handleBlockFallToGround(@Nonnull Block block);

    boolean handleBlockFallDropItem(@Nonnull PersistentDataContainer blockDataContainer,
                                    @Nonnull Item dropItemEntity);
}
