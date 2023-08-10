package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDeath;

public interface FeatureEntityDeath {
    boolean handleEntityDeath(DataEntityDeath data);
}
