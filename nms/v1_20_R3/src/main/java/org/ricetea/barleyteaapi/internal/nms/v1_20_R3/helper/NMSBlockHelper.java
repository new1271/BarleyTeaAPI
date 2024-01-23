package org.ricetea.barleyteaapi.internal.nms.v1_20_R3.helper;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;

public final class NMSBlockHelper {
    public static boolean isPreferredItem(Material type, ItemStack item) {
        Block block = CraftMagicNumbers.getBlock(type);
        if (block == null)
            return false;
        BlockState data = ((CraftBlockData) type.createBlockData()).getState();
        // boolean isCorrectToolForDrops(net.minecraft.world.level.block.state.BlockState) -> b
        return CraftItemStack.asNMSCopy(item).isCorrectToolForDrops(data);
    }
}
