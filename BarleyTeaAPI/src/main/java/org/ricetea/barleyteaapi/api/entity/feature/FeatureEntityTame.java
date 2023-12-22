package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTame;

import javax.annotation.Nonnull;

public interface FeatureEntityTame {
    boolean handleEntityTame(@Nonnull DataEntityTame data);
}
