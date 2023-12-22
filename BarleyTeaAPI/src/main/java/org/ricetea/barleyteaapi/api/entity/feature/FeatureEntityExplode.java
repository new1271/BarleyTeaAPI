package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityExplode;

import javax.annotation.Nonnull;

public interface FeatureEntityExplode {
    boolean handleEntityExplode(@Nonnull DataEntityExplode data);
}
