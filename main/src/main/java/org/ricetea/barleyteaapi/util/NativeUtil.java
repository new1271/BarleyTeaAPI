package org.ricetea.barleyteaapi.util;

import org.bukkit.Bukkit;

import javax.annotation.Nonnull;

public class NativeUtil {
    @Nonnull
    public static String getNMSVersion(){
        String v = Bukkit.getServer().getClass().getPackage().getName();
        return v.substring(v.lastIndexOf('.') + 1);
    }
}
