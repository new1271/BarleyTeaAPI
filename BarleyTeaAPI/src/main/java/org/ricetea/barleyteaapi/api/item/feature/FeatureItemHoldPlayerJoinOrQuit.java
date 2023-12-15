package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldPlayerJoin;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldPlayerQuit;

public interface FeatureItemHoldPlayerJoinOrQuit {
    void handleItemHoldPlayerJoin(@Nonnull DataItemHoldPlayerJoin data);

    void handleItemHoldPlayerQuit(@Nonnull DataItemHoldPlayerQuit data);
}
