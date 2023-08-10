package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDismount;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMount;

public interface FeatureEntityMount {
    boolean handleEntityMount(DataEntityMount data);

    boolean handleEntityBeMounted(DataEntityMount data);

    boolean handleEntityDismount(DataEntityDismount data);

    boolean handleEntityBeDismounted(DataEntityDismount data);
}
