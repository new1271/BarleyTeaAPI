package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemGrindstone;

import javax.annotation.Nonnull;

public interface FeatureItemGrindstone extends ItemFeature {
    boolean handleItemGrindstone(@Nonnull DataItemGrindstone data);
}
