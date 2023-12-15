package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemGotFocus;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemLostFocus;

public interface FeatureItemFocus {
    boolean handleItemGotFocus(@Nonnull DataItemGotFocus data);

    boolean handleItemLostFocus(@Nonnull DataItemLostFocus data);
}
