package org.ricetea.barleyteaapi.api.entity.feature;

import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityBeDismounted;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityBeMounted;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDismount;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMount;

import javax.annotation.Nonnull;

public interface FeatureEntityMount extends EntityFeature {
    boolean handleEntityMount(@Nonnull DataEntityMount data);

    boolean handleEntityBeMounted(@Nonnull DataEntityBeMounted data);

    boolean handleEntityDismount(@Nonnull DataEntityDismount data);

    boolean handleEntityBeDismounted(@Nonnull DataEntityBeDismounted data);
}
