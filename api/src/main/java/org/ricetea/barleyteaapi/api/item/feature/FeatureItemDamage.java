package org.ricetea.barleyteaapi.api.item.feature;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemBroken;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemDamage;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemMend;

import javax.annotation.Nonnull;

public interface FeatureItemDamage extends ItemFeature {
    boolean handleItemDamage(@Nonnull DataItemDamage data);

    boolean handleItemMend(@Nonnull DataItemMend data);

    void handleItemBroken(@Nonnull DataItemBroken data);
}
