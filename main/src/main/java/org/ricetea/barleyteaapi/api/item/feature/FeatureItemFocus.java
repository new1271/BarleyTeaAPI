package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemGotFocus;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemLostFocus;

import javax.annotation.Nonnull;

public interface FeatureItemFocus {
    boolean handleItemGotFocus(@Nonnull DataItemGotFocus data);

    boolean handleItemLostFocus(@Nonnull DataItemLostFocus data);
}
