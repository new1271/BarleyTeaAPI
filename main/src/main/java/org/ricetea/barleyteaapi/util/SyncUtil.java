package org.ricetea.barleyteaapi.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.ricetea.barleyteaapi.BarleyTeaAPI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SyncUtil {

    public static void callInMainThread(@Nonnull Runnable runnable) {
        callInMainThread(runnable, true);
    }

    public static void callInMainThread(@Nullable Plugin plugin, @Nonnull Runnable runnable) {
        callInMainThread(runnable, true);
    }

    public static void callInMainThread(@Nonnull Runnable runnable, boolean force) {
        callInMainThread(null, runnable, force);
    }

    public static void callInMainThread(@Nullable Plugin plugin, @Nonnull Runnable runnable, boolean force) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
            return;
        }
        if (plugin == null && (plugin = BarleyTeaAPI.getInstanceUnsafe()) == null)
            return;
        if (plugin.isEnabled()) {
            Bukkit.getScheduler().runTask(plugin, runnable);
            return;
        }
        if (!force) {
            runnable.run();
        }
    }
}
