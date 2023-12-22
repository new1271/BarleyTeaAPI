package org.ricetea.barleyteaapi.api.block.feature;

import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockEntityChange;

import javax.annotation.Nonnull;

public interface FeatureBlockEntityChange {
    boolean handleBlockEntityChange(@Nonnull DataBlockEntityChange data);
}
