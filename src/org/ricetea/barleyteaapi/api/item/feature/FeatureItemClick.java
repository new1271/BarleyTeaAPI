package org.ricetea.barleyteaapi.api.item.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.item.feature.data.DataItemClickBlock;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemClickEntity;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemClickNothing;
import org.ricetea.barleyteaapi.api.item.feature.state.StateItemClickBlock;

public interface FeatureItemClick {
    boolean handleItemClickNothing(@Nonnull DataItemClickNothing data);

    StateItemClickBlock handleItemClickBlock(@Nonnull DataItemClickBlock data);

    boolean handleItemClickEntity(@Nonnull DataItemClickEntity data);
}
