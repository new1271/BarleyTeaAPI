package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Entity;

public final class DataCommandSummon {

    @Nonnull
    private final Entity entity;

    @Nonnull
    private final String nbt;

    public DataCommandSummon(@Nonnull Entity entity, @Nullable String nbt) {
        this.entity = entity;
        this.nbt = nbt == null ? "" : nbt;
    }

    @Nonnull
    public final Entity getEntity() {
        return entity;
    }

    @Nonnull
    public final String getNBT() {
        return nbt;
    }
}
