package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataCommandGive;

import javax.annotation.Nonnull;

public interface FeatureCommandGive extends ItemFeature {
    boolean handleCommandGive(@Nonnull DataCommandGive data);
}
