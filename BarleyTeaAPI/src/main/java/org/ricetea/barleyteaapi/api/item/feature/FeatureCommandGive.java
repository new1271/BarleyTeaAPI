package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataCommandGive;

import javax.annotation.Nonnull;

public interface FeatureCommandGive {
    boolean handleCommandGive(@Nonnull DataCommandGive data);
}
