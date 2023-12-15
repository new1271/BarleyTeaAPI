package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityAttack;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByNothing;

public interface FeatureEntityDamage {
    boolean handleEntityDamagedByEntity(@Nonnull DataEntityDamagedByEntity data);

    boolean handleEntityDamagedByBlock(@Nonnull DataEntityDamagedByBlock data);

    boolean handleEntityDamagedByNothing(@Nonnull DataEntityDamagedByNothing data);

    boolean handleEntityAttack(@Nonnull DataEntityAttack data);
}
