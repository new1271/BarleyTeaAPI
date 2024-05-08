package org.ricetea.barleyteaapi.api.entity.feature.monitor;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityBeDismounted;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityBeMounted;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDismount;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMount;

import javax.annotation.Nonnull;

public interface FeatureMonitorEntityMount extends EntityMonitorFeature {
    void monitorEntityMount(@Nonnull DataEntityMount data);

    void monitorEntityBeMounted(@Nonnull DataEntityBeMounted data);

    void monitorEntityDismount(@Nonnull DataEntityDismount data);

    void monitorEntityBeDismounted(@Nonnull DataEntityBeDismounted data);
}
