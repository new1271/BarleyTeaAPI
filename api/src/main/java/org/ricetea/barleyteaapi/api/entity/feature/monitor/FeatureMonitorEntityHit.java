package org.ricetea.barleyteaapi.api.entity.feature.monitor;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityHit;

import javax.annotation.Nonnull;

public interface FeatureMonitorEntityHit extends EntityMonitorFeature {
    void monitorEntityHit(@Nonnull DataEntityHit data);
}
