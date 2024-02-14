package org.ricetea.barleyteaapi.api.block.feature;

import org.ricetea.barleyteaapi.api.block.feature.data.*;

import javax.annotation.Nonnull;

public interface FeatureBlockBreak extends BlockFeature {
    boolean handleBlockBreakByPlayer(@Nonnull DataBlockBreakByPlayer data);

    boolean handleBlockBreakByBlockExplode(@Nonnull DataBlockBreakByBlockExplode data);

    boolean handleBlockBreakByEntityExplode(@Nonnull DataBlockBreakByEntityExplode data);

    boolean handleBlockDropByPlayer(@Nonnull DataBlockDropByPlayer data);

    boolean handleBlockDropByEntity(@Nonnull DataBlockDropByEntity data);
}
