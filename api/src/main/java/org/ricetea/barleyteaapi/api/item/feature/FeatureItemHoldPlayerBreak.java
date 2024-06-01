package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldPlayerBreakBlock;

import javax.annotation.Nonnull;

public interface FeatureItemHoldPlayerBreak extends ItemFeature {
    boolean handleItemHoldPlayerBreakBlock(@Nonnull DataItemHoldPlayerBreakBlock data);
}
