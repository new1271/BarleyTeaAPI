package org.ricetea.barleyteaapi.api.entity.feature.monitor;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityBeTargeted;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityLostTarget;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTarget;

import javax.annotation.Nonnull;

public interface FeatureMonitorEntityTarget extends EntityMonitorFeature {
    void monitorEntityTarget(@Nonnull DataEntityTarget data);

    void monitorEntityLostTarget(@Nonnull DataEntityLostTarget data);

    void monitorEntityBeTargeted(@Nonnull DataEntityBeTargeted data);
}
