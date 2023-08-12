package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDismount;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMount;

public interface FeatureEntityMount {
    boolean handleEntityMount(@Nonnull DataEntityMount data);

    boolean handleEntityBeMounted(@Nonnull DataEntityMount data);

    boolean handleEntityDismount(@Nonnull DataEntityDismount data);

    boolean handleEntityBeDismounted(@Nonnull DataEntityDismount data);
}
