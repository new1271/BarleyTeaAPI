package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.*;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;

public interface FeatureItemHoldEntityDamageV2 extends FeatureItemHoldEntityDamage {
    boolean handleItemHoldEntityDamagedByEntity(@Nonnull DataItemHoldEntityDamagedByEntityV2 data);

    boolean handleItemHoldEntityDamagedByBlock(@Nonnull DataItemHoldEntityDamagedByBlockV2 data);

    boolean handleItemHoldEntityDamagedByNothing(@Nonnull DataItemHoldEntityDamagedByNothingV2 data);

    boolean handleItemHoldEntityAttack(@Nonnull DataItemHoldEntityAttackV2 data);

    @Override
    default boolean handleItemHoldEntityDamagedByEntity(@Nonnull DataItemHoldEntityDamagedByEntity data) {
        DataItemHoldEntityDamagedByEntityV2 dataV2 = ObjectUtil.tryCast(data, DataItemHoldEntityDamagedByEntityV2.class);
        if (dataV2 == null)
            dataV2 = new DataItemHoldEntityDamagedByEntityV2(data.getBaseEvent(),
                    data.getItemStack(), data.getEquipmentSlot());
        return handleItemHoldEntityDamagedByEntity(dataV2);
    }

    @Override
    default boolean handleItemHoldEntityDamagedByBlock(@Nonnull DataItemHoldEntityDamagedByBlock data) {
        DataItemHoldEntityDamagedByBlockV2 dataV2 = ObjectUtil.tryCast(data, DataItemHoldEntityDamagedByBlockV2.class);
        if (dataV2 == null)
            dataV2 = new DataItemHoldEntityDamagedByBlockV2(data.getBaseEvent(),
                    data.getItemStack(), data.getEquipmentSlot());
        return handleItemHoldEntityDamagedByBlock(dataV2);
    }

    @Override
    default boolean handleItemHoldEntityDamagedByNothing(@Nonnull DataItemHoldEntityDamagedByNothing data) {
        DataItemHoldEntityDamagedByNothingV2 dataV2 = ObjectUtil.tryCast(data, DataItemHoldEntityDamagedByNothingV2.class);
        if (dataV2 == null)
            dataV2 = new DataItemHoldEntityDamagedByNothingV2(data.getBaseEvent(),
                    data.getItemStack(), data.getEquipmentSlot());
        return handleItemHoldEntityDamagedByNothing(dataV2);
    }

    @Override
    default boolean handleItemHoldEntityAttack(@Nonnull DataItemHoldEntityAttack data) {
        DataItemHoldEntityAttackV2 dataV2 = ObjectUtil.tryCast(data, DataItemHoldEntityAttackV2.class);
        if (dataV2 == null)
            dataV2 = new DataItemHoldEntityAttackV2(data.getBaseEvent(),
                    data.getItemStack(), data.getEquipmentSlot());
        return handleItemHoldEntityAttack(dataV2);
    }
}
