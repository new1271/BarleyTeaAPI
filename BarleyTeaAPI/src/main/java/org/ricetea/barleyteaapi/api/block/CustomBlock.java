package org.ricetea.barleyteaapi.api.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.ricetea.barleyteaapi.api.base.CustomObject;
import org.ricetea.barleyteaapi.api.block.helper.BlockHelper;
import org.ricetea.barleyteaapi.internal.block.registration.BlockRegisterImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CustomBlock extends CustomObject {

    @Nullable
    static CustomBlock get(@Nullable Block block) {
        if (block == null || block.isEmpty())
            return null;
        else {
            BlockRegisterImpl register = BlockRegisterImpl.getInstanceUnsafe();
            if (register == null)
                return null;
            return register.lookup(BlockHelper.getBlockID(block));
        }
    }

    @Nonnull
    Material getOriginalType();

    @Nonnull
    default CustomBlockType getType() {
        return CustomBlockType.get(this);
    }
}
