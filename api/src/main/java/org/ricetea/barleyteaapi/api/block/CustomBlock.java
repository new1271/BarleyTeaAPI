package org.ricetea.barleyteaapi.api.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.ricetea.barleyteaapi.api.base.CustomObject;
import org.ricetea.barleyteaapi.api.block.feature.BlockFeature;
import org.ricetea.barleyteaapi.api.block.helper.BlockHelper;
import org.ricetea.barleyteaapi.api.block.registration.BlockRegister;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public interface CustomBlock extends CustomObject<BlockFeature> {

    @Nullable
    static CustomBlock get(@Nullable Block block) {
        if (block == null || block.isEmpty())
            return null;
        else {
            BlockRegister register = BlockRegister.getInstanceUnsafe();
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

    @Nullable
    default <T extends BlockFeature> T getFeature(@Nonnull Class<T> featureClass) {
        return ObjectUtil.tryCast(this, featureClass);
    }

    @Override
    @Nonnull
    default Collection<Class<? extends BlockFeature>> getFeatures() {
        Class<?>[] interfaces = getClass().getInterfaces();
        ArrayList<Class<? extends BlockFeature>> result = new ArrayList<>(interfaces.length);
        for (Class<?> _interface : interfaces) {
            try {
                Class<? extends BlockFeature> castedInterface = _interface.asSubclass(BlockFeature.class);
                result.add(castedInterface);
            } catch (Exception ignored) {

            }
        }
        return result.stream().collect(Collectors.toUnmodifiableSet());
    }
}
