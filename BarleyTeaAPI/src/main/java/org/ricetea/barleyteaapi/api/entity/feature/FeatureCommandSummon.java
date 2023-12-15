package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataCommandSummon;

public interface FeatureCommandSummon {
    boolean handleCommandSummon(@Nonnull DataCommandSummon data);
}
