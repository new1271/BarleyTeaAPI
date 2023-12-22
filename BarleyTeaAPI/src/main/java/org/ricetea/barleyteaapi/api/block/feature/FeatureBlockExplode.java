package org.ricetea.barleyteaapi.api.block.feature;

import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockExplode;

import javax.annotation.Nonnull;

public interface FeatureBlockExplode {
    boolean handleBlockExplode(@Nonnull DataBlockExplode data);
}
