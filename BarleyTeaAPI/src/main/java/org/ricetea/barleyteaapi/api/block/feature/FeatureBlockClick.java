package org.ricetea.barleyteaapi.api.block.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockClicked;
import org.ricetea.barleyteaapi.api.block.feature.state.StateBlockClicked;

public interface FeatureBlockClick {
    @Nonnull
    StateBlockClicked handleBlockClicked(@Nonnull DataBlockClicked data);
}
