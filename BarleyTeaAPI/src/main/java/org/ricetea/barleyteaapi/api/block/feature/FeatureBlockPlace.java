package org.ricetea.barleyteaapi.api.block.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockPlaceByPlayer;

public interface FeatureBlockPlace {
    boolean handleBlockPlaceByPlayer(@Nonnull DataBlockPlaceByPlayer data);
}
