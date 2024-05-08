package org.ricetea.barleyteaapi.api.entity.feature.monitor;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTame;

import javax.annotation.Nonnull;

public interface FeatureMonitorEntityTame extends EntityMonitorFeature {
    void monitorEntityTame(@Nonnull DataEntityTame data);
}
