package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityLostTarget;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTarget;

public interface FeatureEntityTarget {
    boolean handleEntityTarget(@Nonnull DataEntityTarget data);

    boolean handleEntityLostTarget(@Nonnull DataEntityLostTarget data);

    boolean handleEntityBeTargeted(@Nonnull DataEntityTarget data);
}
