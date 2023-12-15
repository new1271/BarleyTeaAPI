package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataSlimeSplit;

public interface FeatureSlimeSplit {
    boolean handleSlimeSplit(@Nonnull DataSlimeSplit data);
}
