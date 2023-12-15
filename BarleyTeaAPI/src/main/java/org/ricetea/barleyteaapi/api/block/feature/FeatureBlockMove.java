package org.ricetea.barleyteaapi.api.block.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockMove;

public interface FeatureBlockMove {
    boolean handleBlockMove(@Nonnull DataBlockMove data);
}
