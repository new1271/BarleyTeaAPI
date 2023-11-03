package org.ricetea.barleyteaapi.internal.nms.helper;

import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;

public final class NBTItemHelper {
    @Nonnull
    public static NBTTagCompound getNBT(@Nonnull org.bukkit.inventory.ItemStack itemStack) {
        return getNBT(Objects.requireNonNull(CraftItemStack.asNMSCopy(itemStack)));
    }

    @Nonnull
    public static NBTTagCompound getNBT(@Nonnull net.minecraft.world.item.ItemStack itemStack) {
        // net.minecraft.nbt.CompoundTag getTag() -> u
        return Objects.requireNonNull(itemStack.v());
    }

    @Nonnull
    public static net.minecraft.world.item.ItemStack setNBT(@Nonnull net.minecraft.world.item.ItemStack itemStack,
            NBTTagCompound compound) {
        // void setTag(net.minecraft.nbt.CompoundTag) -> c
        itemStack.c(compound);
        return itemStack;
    }

    @Nonnull
    public static org.bukkit.inventory.ItemStack setNBT(@Nonnull org.bukkit.inventory.ItemStack itemStack,
            NBTTagCompound compound) {
        return Objects.requireNonNull(setNBT(Objects.requireNonNull(CraftItemStack.asNMSCopy(itemStack)), compound).getBukkitStack());
    }

    @Nonnull
    public static org.bukkit.inventory.ItemStack castBukkitItemStack(@Nonnull net.minecraft.world.item.ItemStack stack) {
        return Objects.requireNonNull(CraftItemStack.asCraftMirror(stack));
    }
}
