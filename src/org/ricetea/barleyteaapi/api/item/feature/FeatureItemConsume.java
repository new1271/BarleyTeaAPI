package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemConsume;

public interface FeatureItemConsume {
    boolean handleItemConsume(@Nonnull DataItemConsume data);
}
