package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemWear;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemWearOff;

import javax.annotation.Nonnull;

public interface FeatureItemWear extends ItemFeature {
    void handleItemWear(@Nonnull DataItemWear data);

    void handleItemWearOff(@Nonnull DataItemWearOff data);
}
