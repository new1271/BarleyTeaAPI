package org.ricetea.barleyteaapi.api.entity.feature.monitor;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityShoot;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityShotBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityShotEntity;

import javax.annotation.Nonnull;

public interface FeatureMonitorEntityShoot extends EntityMonitorFeature {
    void monitorEntityShoot(@Nonnull DataEntityShoot data);

    void monitorShotEntity(@Nonnull DataEntityShotEntity data);

    void monitorShotBlock(@Nonnull DataEntityShotBlock data);
}
