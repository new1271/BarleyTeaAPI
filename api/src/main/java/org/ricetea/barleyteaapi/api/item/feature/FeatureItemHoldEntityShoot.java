package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityShoot;

import javax.annotation.Nonnull;

public interface FeatureItemHoldEntityShoot extends ItemFeature {
    boolean handleItemHoldEntityShoot(@Nonnull DataItemHoldEntityShoot data);
}
