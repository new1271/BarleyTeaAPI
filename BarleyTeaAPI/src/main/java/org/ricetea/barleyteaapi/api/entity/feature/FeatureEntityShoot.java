package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityShoot;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityShotBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityShotEntity;

public interface FeatureEntityShoot {
    boolean handleEntityShoot(@Nonnull DataEntityShoot data);

    boolean handleShotEntity(@Nonnull DataEntityShotEntity data);

    boolean handleShotBlock(@Nonnull DataEntityShotBlock data);
}