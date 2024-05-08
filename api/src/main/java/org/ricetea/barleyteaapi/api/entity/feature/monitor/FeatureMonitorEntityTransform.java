package org.ricetea.barleyteaapi.api.entity.feature.monitor;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTransform;

import javax.annotation.Nonnull;

public interface FeatureMonitorEntityTransform extends EntityMonitorFeature {
    void monitorEntityTransform(@Nonnull DataEntityTransform data);
}
