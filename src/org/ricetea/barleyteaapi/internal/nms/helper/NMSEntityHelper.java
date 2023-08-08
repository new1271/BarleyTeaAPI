package org.ricetea.barleyteaapi.internal.nms.helper;

import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;

public final class NMSEntityHelper {

    public static void dropItem(org.bukkit.entity.Player player, org.bukkit.inventory.ItemStack item) {
        //net.minecraft.world.entity.item.ItemEntity drop(net.minecraft.world.item.ItemStack,boolean,boolean) -> a
        ((CraftPlayer) player).getHandle().a(CraftItemStack.asNMSCopy(item), true, true);
    }

    public static net.minecraft.world.entity.Entity getNmsEntity(org.bukkit.entity.Entity entity) {
        return ((CraftEntity) entity).getHandle();
    }
}
