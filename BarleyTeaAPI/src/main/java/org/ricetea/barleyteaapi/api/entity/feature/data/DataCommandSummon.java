package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    public Entity getEntity() {
        return entity;
    }

    @Nonnull
    public String getNBT() {
        return nbt;
    }
}
