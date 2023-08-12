package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataKillEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataKillPlayer;

public interface FeatureKillEntity {
    boolean handleKillEntity(@Nonnull DataKillEntity data);

    boolean handleKillPlayer(@Nonnull DataKillPlayer data);
}
