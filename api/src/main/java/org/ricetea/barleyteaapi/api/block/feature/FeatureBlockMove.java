package org.ricetea.barleyteaapi.api.block.feature;

import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockMove;

import javax.annotation.Nonnull;

public interface FeatureBlockMove extends BlockFeature {
    boolean handleBlockMove(@Nonnull DataBlockMove data);
}
