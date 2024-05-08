package org.ricetea.barleyteaapi.api.entity.feature.monitor;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDeath;

import javax.annotation.Nonnull;

public interface FeatureMonitorEntityDeath extends EntityMonitorFeature {
    void monitorEntityDeath(@Nonnull DataEntityDeath data);
}
