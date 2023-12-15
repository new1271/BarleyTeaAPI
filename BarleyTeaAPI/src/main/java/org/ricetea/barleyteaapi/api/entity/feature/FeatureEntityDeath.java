package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDeath;

public interface FeatureEntityDeath {
    boolean handleEntityDeath(@Nonnull DataEntityDeath data);
}
