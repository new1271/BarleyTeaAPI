package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemGrindstone;

public interface FeatureItemGrindstone {
    boolean handleItemGrindstone(@Nonnull DataItemGrindstone data);
}
