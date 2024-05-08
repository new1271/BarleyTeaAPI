package org.ricetea.barleyteaapi.api.entity.feature.monitor;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataKillEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataKillPlayer;

import javax.annotation.Nonnull;

public interface FeatureMonitorKillEntity extends EntityMonitorFeature {
    void monitorKillEntity(@Nonnull DataKillEntity data);

    void monitorKillPlayer(@Nonnull DataKillPlayer data);
}
