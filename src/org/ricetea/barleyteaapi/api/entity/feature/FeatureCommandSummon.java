package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Entity;

public interface FeatureCommandSummon {
    boolean handleCommandSummon(@Nonnull Entity entitySummoned, @Nullable String nbt);
}
