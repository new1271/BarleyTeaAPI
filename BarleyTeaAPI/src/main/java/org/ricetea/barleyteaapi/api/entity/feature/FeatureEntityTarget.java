package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityBeTargeted;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityLostTarget;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTarget;

import javax.annotation.Nonnull;

public interface FeatureEntityTarget {
    boolean handleEntityTarget(@Nonnull DataEntityTarget data);

    boolean handleEntityLostTarget(@Nonnull DataEntityLostTarget data);

    boolean handleEntityBeTargeted(@Nonnull DataEntityBeTargeted data);
}
