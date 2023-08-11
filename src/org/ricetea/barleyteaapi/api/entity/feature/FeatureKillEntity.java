package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataKillEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataKillPlayer;

public interface FeatureKillEntity {
    boolean handleKillEntity(DataKillEntity data);

    boolean handleKillPlayer(DataKillPlayer data);
}
