package org.ricetea.barleyteaapi.internal.nms.v1_20_R2.helper;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class NBTItemHelper {
    @Nonnull
    public static CompoundTag getNBT(@Nonnull org.bukkit.inventory.ItemStack itemStack) {
        return getNBT(Objects.requireNonNull(CraftItemStack.asNMSCopy(itemStack)));
    }

    @Nonnull
    public static CompoundTag getNBT(@Nonnull net.minecraft.world.item.ItemStack itemStack) {
        // net.minecraft.nbt.CompoundTag getTag() -> u
        return Objects.requireNonNull(itemStack.getTag());
    }

    @Nonnull
    public static net.minecraft.world.item.ItemStack setNBT(@Nonnull net.minecraft.world.item.ItemStack itemStack,
                                                            CompoundTag compound) {
        // void setTag(net.minecraft.nbt.CompoundTag) -> c
        itemStack.setTag(compound);
        return itemStack;
    }

    @Nonnull
    public static org.bukkit.inventory.ItemStack setNBT(@Nonnull org.bukkit.inventory.ItemStack itemStack,
                                                        CompoundTag compound) {
        return Objects.requireNonNull(setNBT(Objects.requireNonNull(CraftItemStack.asNMSCopy(itemStack)), compound).getBukkitStack());
    }

    @Nonnull
    public static org.bukkit.inventory.ItemStack castBukkitItemStack(@Nonnull net.minecraft.world.item.ItemStack stack) {
        return Objects.requireNonNull(CraftItemStack.asCraftMirror(stack));
    }
}
