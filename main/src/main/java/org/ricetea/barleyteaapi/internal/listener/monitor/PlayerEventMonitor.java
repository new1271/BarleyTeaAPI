package org.ricetea.barleyteaapi.internal.listener.monitor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.util.PlayerUtil;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class PlayerEventMonitor implements Listener {
    private static final Lazy<PlayerEventMonitor> inst = Lazy.create(PlayerEventMonitor::new);

    private PlayerEventMonitor() {
    }

    @Nonnull
    public static PlayerEventMonitor getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void listenPlayerJoin(PlayerJoinEvent event) {
        if (event == null)
            return;
        PlayerUtil.updateOnlinePlayerSnapshot();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void listenPlayerQuit(PlayerQuitEvent event) {
        if (event == null)
            return;
        PlayerUtil.updateOnlinePlayerSnapshot();
    }
}
