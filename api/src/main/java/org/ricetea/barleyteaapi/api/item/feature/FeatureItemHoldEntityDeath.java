package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDeath;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldPlayerDeath;

import javax.annotation.Nonnull;

public interface FeatureItemHoldEntityDeath extends ItemFeature {

    boolean handleItemHoldEntityDeath(@Nonnull DataItemHoldEntityDeath data);

    boolean handleItemHoldPlayerDeath(@Nonnull DataItemHoldPlayerDeath data);
}
