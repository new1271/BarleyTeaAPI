package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemAnvilCombine;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemAnvilRename;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemAnvilRepair;

import javax.annotation.Nonnull;

public interface FeatureItemAnvil {
    boolean handleItemAnvilRename(@Nonnull DataItemAnvilRename data);

    boolean handleItemAnvilCombine(@Nonnull DataItemAnvilCombine data);

    boolean handleItemAnvilRepair(@Nonnull DataItemAnvilRepair data);
}
