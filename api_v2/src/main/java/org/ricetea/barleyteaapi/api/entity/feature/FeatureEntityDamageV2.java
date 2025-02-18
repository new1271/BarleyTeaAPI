package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.*;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;

public interface FeatureEntityDamageV2 extends FeatureEntityDamage {

    boolean handleEntityDamagedByEntity(@Nonnull DataEntityDamagedByEntityV2 data);

    boolean handleEntityDamagedByBlock(@Nonnull DataEntityDamagedByBlockV2 data);

    boolean handleEntityDamagedByNothing(@Nonnull DataEntityDamagedByNothingV2 data);

    boolean handleEntityAttack(@Nonnull DataEntityAttackV2 data);

    @Override
    default boolean handleEntityDamagedByEntity(@Nonnull DataEntityDamagedByEntity data) {
        DataEntityDamagedByEntityV2 dataV2 = ObjectUtil.tryCast(data, DataEntityDamagedByEntityV2.class);
        if (dataV2 == null)
            dataV2 = new DataEntityDamagedByEntityV2(data.getBaseEvent());
        return handleEntityDamagedByEntity(dataV2);
    }

    @Override
    default boolean handleEntityDamagedByBlock(@Nonnull DataEntityDamagedByBlock data) {
        DataEntityDamagedByBlockV2 dataV2 = ObjectUtil.tryCast(data, DataEntityDamagedByBlockV2.class);
        if (dataV2 == null)
            dataV2 = new DataEntityDamagedByBlockV2(data.getBaseEvent());
        return handleEntityDamagedByBlock(dataV2);
    }

    @Override
    default boolean handleEntityDamagedByNothing(@Nonnull DataEntityDamagedByNothing data) {
        DataEntityDamagedByNothingV2 dataV2 = ObjectUtil.tryCast(data, DataEntityDamagedByNothingV2.class);
        if (dataV2 == null)
            dataV2 = new DataEntityDamagedByNothingV2(data.getBaseEvent());
        return handleEntityDamagedByNothing(dataV2);
    }

    @Override
    default boolean handleEntityAttack(@Nonnull DataEntityAttack data) {
        DataEntityAttackV2 dataV2 = ObjectUtil.tryCast(data, DataEntityAttackV2.class);
        if (dataV2 == null)
            dataV2 = new DataEntityAttackV2(data.getBaseEvent());
        return handleEntityAttack(dataV2);
    }
}
