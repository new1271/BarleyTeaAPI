package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemConsume;

import javax.annotation.Nonnull;

public interface FeatureItemConsume extends ItemFeature {
    boolean handleItemConsume(@Nonnull DataItemConsume data);
}
