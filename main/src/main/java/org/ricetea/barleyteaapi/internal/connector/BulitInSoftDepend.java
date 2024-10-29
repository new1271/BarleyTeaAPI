package org.ricetea.barleyteaapi.internal.connector;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public enum BulitInSoftDepend implements Supplier<String> {
    ExcellentEnchants("ExcellentEnchants"),
    ProtocolLib("ProtocolLib"),
    Geyser("Geyser-Spigot"),
    InteractiveChat("InteractiveChat");

    private final String pluginName;

    BulitInSoftDepend(@Nonnull String pluginName) {
        this.pluginName = pluginName;
    }

    @Nonnull
    public String getPluginName() {
        return pluginName;
    }

    @Override
    public String toString() {
        return getPluginName();
    }

    @Override
    public String get() {
        return getPluginName();
    }
}
