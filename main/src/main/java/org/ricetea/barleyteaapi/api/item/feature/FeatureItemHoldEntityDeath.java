package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDeath;

import javax.annotation.Nonnull;

public interface FeatureItemHoldEntityDeath {

    boolean handleItemHoldEntityDeath(@Nonnull DataItemHoldEntityDeath data);
}
