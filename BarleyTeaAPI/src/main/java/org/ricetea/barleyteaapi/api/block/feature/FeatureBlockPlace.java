package org.ricetea.barleyteaapi.api.block.feature;

import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockPlaceByPlayer;

import javax.annotation.Nonnull;

public interface FeatureBlockPlace {
    boolean handleBlockPlaceByPlayer(@Nonnull DataBlockPlaceByPlayer data);
}
