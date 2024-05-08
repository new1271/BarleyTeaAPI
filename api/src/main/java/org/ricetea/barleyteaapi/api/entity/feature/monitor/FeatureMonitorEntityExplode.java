package org.ricetea.barleyteaapi.api.entity.feature.monitor;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityExplode;

import javax.annotation.Nonnull;

public interface FeatureMonitorEntityExplode extends EntityMonitorFeature {
    void monitorEntityExplode(@Nonnull DataEntityExplode data);
}
