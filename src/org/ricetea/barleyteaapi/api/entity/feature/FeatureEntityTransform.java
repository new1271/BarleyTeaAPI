package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTransform;

public interface FeatureEntityTransform {
    boolean handleEntityTransform(DataEntityTransform data);
}
