package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldPlayerPlaceBlock;

import javax.annotation.Nonnull;

public interface FeatureItemHoldPlayerPlace extends ItemFeature {
    boolean handleItemHoldPlayerPlaceBlock(@Nonnull DataItemHoldPlayerPlaceBlock data);
}
