package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityMove;

import javax.annotation.Nonnull;

public interface FeatureItemHoldEntityMove extends ItemFeature {

    boolean handleItemHoldEntityEntityMove(@Nonnull DataItemHoldEntityMove data);
}
