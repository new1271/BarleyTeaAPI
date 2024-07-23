package org.ricetea.barleyteaapi.util;

import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.internal.nms.INMSItemHelper;
import org.ricetea.barleyteaapi.api.internal.nms.NMSVersion;

import javax.annotation.Nullable;

public class NativeUtil {
    @Nullable
    public static ItemStack fromShowItem(@Nullable HoverEvent.ShowItem showItem) {
        if (showItem == null)
            return null;
        BinaryTagHolder nbtHolder = showItem.nbt();
        if (nbtHolder == null)
            return null;
        INMSItemHelper helper = INMSItemHelper.getInstanceUnsafe();
        if (helper == null)
            return null;
        NMSVersion version = NMSVersion.getCurrent();
        if (version == null || !version.isValid())
            return null;
        String rawNbt;
        if (NMSVersion.getCurrent().getVersion() < NMSVersion.v1_20_R4.getVersion()) {
            rawNbt = "{\"id\":\"" + showItem.item() + "\", \"Count\":" + showItem.count() + ", \"tag\": "
                    + nbtHolder.string() + "}";
        } else {
            rawNbt = "{\"id\":\"" + showItem.item() + "\", \"Count\":" + showItem.count() + ", \"components\": "
                    + nbtHolder.string() + "}";
        }
        return helper.createItemStackFromNbtString(rawNbt);
    }
}
