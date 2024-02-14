package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataSlimeSplit;

import javax.annotation.Nonnull;

public interface FeatureSlimeSplit extends EntityFeature {
    boolean handleSlimeSplit(@Nonnull DataSlimeSplit data);
}
