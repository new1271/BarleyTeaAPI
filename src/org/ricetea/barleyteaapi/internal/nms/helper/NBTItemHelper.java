package org.ricetea.barleyteaapi.internal.nms.helper;

import net.minecraft.nbt.NBTTagCompound;

import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;

public final class NBTItemHelper {
    public static NBTTagCompound getNBT(org.bukkit.inventory.ItemStack itemStack) {
        return getNBT(CraftItemStack.asNMSCopy(itemStack));
    }

    public static NBTTagCompound getNBT(net.minecraft.world.item.ItemStack itemStack) {
        // net.minecraft.nbt.CompoundTag getTag() -> u
        return itemStack.v();
    }

    public static net.minecraft.world.item.ItemStack setNBT(net.minecraft.world.item.ItemStack itemStack,
            NBTTagCompound compound) {
        // void setTag(net.minecraft.nbt.CompoundTag) -> c
        itemStack.c(compound);
        return itemStack;
    }

    public static org.bukkit.inventory.ItemStack setNBT(org.bukkit.inventory.ItemStack itemStack,
            NBTTagCompound compound) {
        return setNBT(CraftItemStack.asNMSCopy(itemStack), compound).getBukkitStack();
    }

    public static org.bukkit.inventory.ItemStack castBukkitItemStack(net.minecraft.world.item.ItemStack stack) {
        return CraftItemStack.asCraftMirror(stack);
    }
}
