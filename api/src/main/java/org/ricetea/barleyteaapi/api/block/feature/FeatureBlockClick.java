package org.ricetea.barleyteaapi.api.block.feature;

import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockClicked;
import org.ricetea.barleyteaapi.api.block.feature.state.StateBlockClicked;

import javax.annotation.Nonnull;

public interface FeatureBlockClick extends BlockFeature {
    @Nonnull
    StateBlockClicked handleBlockClicked(@Nonnull DataBlockClicked data);
}
