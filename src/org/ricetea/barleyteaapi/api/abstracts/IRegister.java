package org.ricetea.barleyteaapi.api.abstracts;

import javax.annotation.Nonnull;

import org.bukkit.Keyed;

public interface IRegister<T extends Keyed> {
    void register(@Nonnull T key);

    void unregister(@Nonnull T key);
}
