package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemSlotFilter;

import javax.annotation.Nonnull;

public interface FeatureItemSlotFilter extends ItemFeature {
    boolean handleItemSlotFilter(@Nonnull DataItemSlotFilter data);
}
