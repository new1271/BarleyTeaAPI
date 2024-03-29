package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityAttack;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDamagedByBlock;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDamagedByEntity;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDamagedByNothing;

import javax.annotation.Nonnull;

public interface FeatureItemHoldEntityDamage extends ItemFeature {
    boolean handleItemHoldEntityDamagedByEntity(@Nonnull DataItemHoldEntityDamagedByEntity data);

    boolean handleItemHoldEntityDamagedByBlock(@Nonnull DataItemHoldEntityDamagedByBlock data);

    boolean handleItemHoldEntityDamagedByNothing(@Nonnull DataItemHoldEntityDamagedByNothing data);

    boolean handleItemHoldEntityAttack(@Nonnull DataItemHoldEntityAttack data);
}
