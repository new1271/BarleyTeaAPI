package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;
import org.ricetea.barleyteaapi.api.item.feature.data.DataCommandGive;

public interface FeatureCommandGive {
    boolean handleCommandGive(@Nonnull DataCommandGive data);
}
