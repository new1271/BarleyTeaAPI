package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDeath;

public interface FeatureItemHoldEntityDeath {

    boolean handleItemHoldEntityDeath(@Nonnull DataItemHoldEntityDeath data);
}
