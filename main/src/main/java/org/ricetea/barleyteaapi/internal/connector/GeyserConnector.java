package org.ricetea.barleyteaapi.internal.connector;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.geysermc.geyser.api.GeyserApi;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.util.connector.SoftDependConnector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ApiStatus.Internal
public final class GeyserConnector implements SoftDependConnector {

    @Nullable
    private GeyserApi api;

    @Override
    public void onEnable(@Nonnull Plugin plugin) {
        api = GeyserApi.api();
    }

    @Override
    public void onDisable() {
        api = null;
    }

    public boolean isBedrockPlayer(@Nonnull Player player) {
        if (api == null)
            return false;
        return api.isBedrockPlayer(player.getUniqueId());
    }
}
