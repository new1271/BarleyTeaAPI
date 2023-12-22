package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataKillEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataKillPlayer;

import javax.annotation.Nonnull;

public interface FeatureKillEntity {
    boolean handleKillEntity(@Nonnull DataKillEntity data);

    boolean handleKillPlayer(@Nonnull DataKillPlayer data);
}
