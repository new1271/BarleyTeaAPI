package org.ricetea.barleyteaapi.api.block.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockExplode;

public interface FeatureBlockExplode {
    boolean handleBlockExplode(@Nonnull DataBlockExplode data);
}
