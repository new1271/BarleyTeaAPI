package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldPlayerJoin;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldPlayerQuit;

import javax.annotation.Nonnull;

public interface FeatureItemHoldPlayerJoinOrQuit {
    void handleItemHoldPlayerJoin(@Nonnull DataItemHoldPlayerJoin data);

    void handleItemHoldPlayerQuit(@Nonnull DataItemHoldPlayerQuit data);
}
