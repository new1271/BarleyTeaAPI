package org.ricetea.barleyteaapi.api.block.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockEntityChange;

public interface FeatureBlockEntityChange {
    boolean handleBlockEntityChange(@Nonnull DataBlockEntityChange data);
}
