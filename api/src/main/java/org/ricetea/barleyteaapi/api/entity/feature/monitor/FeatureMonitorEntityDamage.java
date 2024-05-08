package org.ricetea.barleyteaapi.api.entity.feature.monitor;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityAttack;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByNothing;

import javax.annotation.Nonnull;

public interface FeatureMonitorEntityDamage extends EntityMonitorFeature {
    void monitorEntityDamagedByEntity(@Nonnull DataEntityDamagedByEntity data);

    void monitorEntityDamagedByBlock(@Nonnull DataEntityDamagedByBlock data);

    void monitorEntityDamagedByNothing(@Nonnull DataEntityDamagedByNothing data);

    void monitorEntityAttack(@Nonnull DataEntityAttack data);
}
