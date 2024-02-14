package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemDisplay;

import javax.annotation.Nonnull;

public interface FeatureItemDisplay extends ItemFeature {
    void handleItemDisplay(@Nonnull DataItemDisplay data);
}
