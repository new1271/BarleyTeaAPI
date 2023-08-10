package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityLostTarget;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTarget;

public interface FeatureEntityTarget {
    boolean handleEntityTarget(DataEntityTarget data);

    boolean handleEntityLostTarget(DataEntityLostTarget data);

    boolean handleEntityBeTargeted(DataEntityTarget data);
}
