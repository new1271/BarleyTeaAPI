package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityBeDismounted;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityBeMounted;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDismount;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMount;

public interface FeatureEntityMount {
    boolean handleEntityMount(@Nonnull DataEntityMount data);

    boolean handleEntityBeMounted(@Nonnull DataEntityBeMounted data);

    boolean handleEntityDismount(@Nonnull DataEntityDismount data);

    boolean handleEntityBeDismounted(@Nonnull DataEntityBeDismounted data);
}