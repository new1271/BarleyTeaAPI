package org.ricetea.barleyteaapi.internal.nms.helper;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public final class NMSBlockHelper {
    public static boolean isPreferredItem(Material type, ItemStack item) {
        Block block = CraftMagicNumbers.getBlock(type);
        if (block == null)
            return false;
        IBlockData data = ((CraftBlockData) type.createBlockData()).getState();
        // boolean isCorrectToolForDrops(net.minecraft.world.level.block.state.BlockState) -> b
        return CraftItemStack.asNMSCopy(item).b(data);
    }
}
