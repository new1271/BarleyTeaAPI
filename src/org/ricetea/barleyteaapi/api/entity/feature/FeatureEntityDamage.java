package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByNothing;

public interface FeatureEntityDamage {
    boolean handleEntityDamagedByEntity(DataEntityDamagedByEntity data);

    boolean handleEntityDamagedByBlock(DataEntityDamagedByBlock data);

    boolean handleEntityDamagedByNothing(DataEntityDamagedByNothing data);

    boolean handleEntityAttack(DataEntityDamagedByEntity data);
}
