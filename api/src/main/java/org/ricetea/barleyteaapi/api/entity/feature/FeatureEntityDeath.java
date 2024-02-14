package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDeath;

import javax.annotation.Nonnull;

public interface FeatureEntityDeath extends EntityFeature {
    boolean handleEntityDeath(@Nonnull DataEntityDeath data);
}
