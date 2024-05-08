package org.ricetea.barleyteaapi.api.entity.feature.monitor;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataSlimeSplit;

import javax.annotation.Nonnull;

public interface FeatureMonitorSlimeSplit extends EntityMonitorFeature {
    void monitorSlimeSplit(@Nonnull DataSlimeSplit data);
}
