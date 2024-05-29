package org.ricetea.barleyteaapi.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.ricetea.barleyteaapi.BarleyTeaAPI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SyncUtil {

    public static boolean callInMainThread(@Nonnull Runnable runnable) {
        return callInMainThread(runnable, true);
    }

    public static boolean callInMainThread(@Nullable Plugin plugin, @Nonnull Runnable runnable) {
        return callInMainThread(runnable, true);
    }

    public static boolean callInMainThread(@Nonnull Runnable runnable, boolean force) {
        return callInMainThread(null, runnable, force);
    }

    public static boolean callInMainThread(@Nullable Plugin plugin, @Nonnull Runnable runnable, boolean force) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
            return true;
        }
        if (plugin == null && (plugin = BarleyTeaAPI.getInstanceUnsafe()) == null)
            return true;
        if (plugin.isEnabled()) {
            Bukkit.getScheduler().runTask(plugin, runnable);
            return true;
        }
        if (!force) {
            runnable.run();
            return true;
        }
        return false;
    }
}
