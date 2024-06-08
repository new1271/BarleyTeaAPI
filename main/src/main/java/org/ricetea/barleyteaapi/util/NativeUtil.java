package org.ricetea.barleyteaapi.util;

import org.bukkit.Bukkit;

import javax.annotation.Nonnull;

public class NativeUtil {
    @Nonnull
    public static String getNMSVersion(){
        Class<?> clazz;
        try {
            clazz = Class.forName("org.bukkit.craftbukkit");
        } catch (Exception ignored) {
            clazz = null;
        }
        if (clazz == null) {
            String v = Bukkit.getServer().getClass().getPackage().getName();
            return v.substring(v.lastIndexOf('.') + 1);
        }
        return Bukkit.getBukkitVersion();
    }
}
