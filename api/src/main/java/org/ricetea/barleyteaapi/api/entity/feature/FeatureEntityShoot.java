package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityShoot;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityShotBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityShotEntity;

import javax.annotation.Nonnull;

public interface FeatureEntityShoot {
    boolean handleEntityShoot(@Nonnull DataEntityShoot data);

    boolean handleShotEntity(@Nonnull DataEntityShotEntity data);

    boolean handleShotBlock(@Nonnull DataEntityShotBlock data);
}
