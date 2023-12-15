package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityMove;

public interface FeatureItemHoldEntityMove {

    boolean handleItemHoldEntityEntityMove(@Nonnull DataItemHoldEntityMove data);
}
