package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemEnchant;

public interface FeatureItemEnchant {
    boolean handleItemEnchant(@Nonnull DataItemEnchant data);
}
