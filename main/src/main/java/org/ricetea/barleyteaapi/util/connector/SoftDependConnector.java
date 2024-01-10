package org.ricetea.barleyteaapi.util.connector;

import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;

public interface SoftDependConnector {
    void onEnable(@Nonnull Plugin plugin);

    void onDisable();
}
