package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemDisplay;

public interface FeatureItemDisplay {
    void handleItemDisplay(@Nonnull DataItemDisplay data);
}