package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemEnchant;

import javax.annotation.Nonnull;

public interface FeatureItemEnchant {
    boolean handleItemEnchant(@Nonnull DataItemEnchant data);
}
