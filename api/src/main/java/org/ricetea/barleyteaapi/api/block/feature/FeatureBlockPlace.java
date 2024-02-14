package org.ricetea.barleyteaapi.api.block.feature;

import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockPlaceByPlayer;

import javax.annotation.Nonnull;

public interface FeatureBlockPlace extends BlockFeature {
    boolean handleBlockPlaceByPlayer(@Nonnull DataBlockPlaceByPlayer data);
}
