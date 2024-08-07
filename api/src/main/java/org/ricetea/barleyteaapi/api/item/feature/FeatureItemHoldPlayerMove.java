package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldPlayerMove;

import javax.annotation.Nonnull;

public interface FeatureItemHoldPlayerMove extends ItemFeature {

    boolean handleItemHoldPlayerMove(@Nonnull DataItemHoldPlayerMove data);
}
