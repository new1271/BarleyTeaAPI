package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldPlayerMove;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldPlayerMove;
import org.ricetea.barleyteaapi.internal.linker.ItemFeatureLinker;
import org.ricetea.utils.Constants;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
@ApiStatus.Internal
public final class PlayerMoveListener implements Listener {
    //This listener will cause lags, so it need lazy-loading

    private static final Lazy<PlayerMoveListener> inst = Lazy.create(PlayerMoveListener::new);

    private final Object _syncRoot = new Object();

    private final AtomicBoolean loaded;

    private PlayerMoveListener() {
        loaded = new AtomicBoolean(false);
    }

    @Nonnull
    public static PlayerMoveListener getInstance() {
        return inst.get();
    }

    @Nullable
    public static PlayerMoveListener getInstanceUnsafe() {
        return inst.getUnsafe();
    }

    public void tryRegisterEvents() {
        if (loaded.get())
            return;
        synchronized (_syncRoot) {
            BarleyTeaAPI api = BarleyTeaAPI.getInstanceUnsafe();
            if (api == null)
                return;
            try {
                Bukkit.getPluginManager().registerEvents(this, api);
                loaded.set(true);
            } catch (Exception ignored) {
            }
        }
    }

    public void tryUnregisterEvents() {
        if (!loaded.get())
            return;
        synchronized (_syncRoot) {
            try {
                HandlerList.unregisterAll(this);
                loaded.set(false);
            } catch (Exception ignored) {
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenPlayerMove(PlayerMoveEvent event) {
        if (event == null || event.isCancelled())
            return;
        if (!ItemFeatureLinker.forEachEquipmentCancellable(event.getPlayer(), event,
                Constants.ALL_SLOTS, FeatureItemHoldPlayerMove.class,
                FeatureItemHoldPlayerMove::handleItemHoldPlayerMove, DataItemHoldPlayerMove::new))
            event.setCancelled(true);
    }
}
