package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemWear;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemWearOff;

public interface FeatureItemWear {
    void handleItemWear(@Nonnull DataItemWear data);

    void handleItemWearOff(@Nonnull DataItemWearOff data);
}
