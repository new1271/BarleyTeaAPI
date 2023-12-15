package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityKillEntity;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityKillPlayer;

public interface FeatureItemHoldEntityKill {

    boolean handleItemHoldEntityKillEntity(@Nonnull DataItemHoldEntityKillEntity data);

    boolean handleItemHoldEntityKillPlayer(@Nonnull DataItemHoldEntityKillPlayer data);
}
