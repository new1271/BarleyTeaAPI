package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldPlayerPlaceBlock;

public interface FeatureItemHoldPlayerPlace {
    boolean handleItemHoldPlayerPlaceBlock(@Nonnull DataItemHoldPlayerPlaceBlock data);
}
