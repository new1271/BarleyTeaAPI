package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataCommandSummon;

import javax.annotation.Nonnull;

public interface FeatureCommandSummon {
    boolean handleCommandSummon(@Nonnull DataCommandSummon data);
}
