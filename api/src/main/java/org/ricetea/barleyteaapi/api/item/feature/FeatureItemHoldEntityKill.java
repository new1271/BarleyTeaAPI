package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityKillEntity;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityKillPlayer;

import javax.annotation.Nonnull;

public interface FeatureItemHoldEntityKill extends ItemFeature {

    boolean handleItemHoldEntityKillEntity(@Nonnull DataItemHoldEntityKillEntity data);

    boolean handleItemHoldEntityKillPlayer(@Nonnull DataItemHoldEntityKillPlayer data);
}
