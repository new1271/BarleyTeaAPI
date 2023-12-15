package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTame;

public interface FeatureEntityTame {
    boolean handleEntityTame(@Nonnull DataEntityTame data);
}
