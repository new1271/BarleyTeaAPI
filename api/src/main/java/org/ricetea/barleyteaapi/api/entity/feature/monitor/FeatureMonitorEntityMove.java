package org.ricetea.barleyteaapi.api.entity.feature.monitor;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMove;

import javax.annotation.Nonnull;

public interface FeatureMonitorEntityMove extends EntityMonitorFeature {
    void monitorEntityMove(@Nonnull DataEntityMove data);
}
