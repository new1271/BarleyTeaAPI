package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemBroken;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemDamage;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemMend;

public interface FeatureItemDamage {
    boolean handleItemDamage(@Nonnull DataItemDamage data);

    boolean handleItemMend(@Nonnull DataItemMend data);

    void handleItemBroken(@Nonnull DataItemBroken data);
}
