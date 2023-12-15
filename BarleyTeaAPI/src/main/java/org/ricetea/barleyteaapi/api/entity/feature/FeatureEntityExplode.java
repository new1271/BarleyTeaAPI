package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityExplode;

public interface FeatureEntityExplode {
    boolean handleEntityExplode(@Nonnull DataEntityExplode data);
}
