package org.ricetea.barleyteaapi.api.block.feature;

import javax.annotation.Nonnull;

import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockBreakByBlockExplode;
import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockBreakByEntityExplode;
import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockBreakByPlayer;
import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockDropByEntity;
import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockDropByPlayer;

public interface FeatureBlockBreak {
    boolean handleBlockBreakByPlayer(@Nonnull DataBlockBreakByPlayer data);

    boolean handleBlockBreakByBlockExplode(@Nonnull DataBlockBreakByBlockExplode data);

    boolean handleBlockBreakByEntityExplode(@Nonnull DataBlockBreakByEntityExplode data);

    boolean handleBlockDropByPlayer(@Nonnull DataBlockDropByPlayer data);

    boolean handleBlockDropByEntity(@Nonnull DataBlockDropByEntity data);
}
