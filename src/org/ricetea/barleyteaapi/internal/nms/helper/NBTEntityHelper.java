package org.ricetea.barleyteaapi.internal.nms.helper;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;

import net.minecraft.nbt.NBTTagCompound;

public final class NBTEntityHelper {
    @Nonnull
    public static NBTTagCompound getNBT(@Nonnull org.bukkit.entity.Entity entity) {
        CraftEntity craftEntity = (CraftEntity) entity;
        return getNBT(Objects.requireNonNull(craftEntity.getHandle()));
    }

    @Nonnull
    public static NBTTagCompound getNBT(@Nonnull net.minecraft.world.entity.Entity entity) {
        // net.minecraft.nbt.CompoundTag saveWithoutId(net.minecraft.nbt.CompoundTag) -> f
        return Objects.requireNonNull(entity.f(new NBTTagCompound()));
    }

    public static void setNBT(@Nonnull org.bukkit.entity.Entity entity, @Nonnull NBTTagCompound compound) {
        CraftEntity craftEntity = (CraftEntity) entity;
        setNBT(Objects.requireNonNull(craftEntity.getHandle()), compound);
    }

    public static void setNBT(@Nonnull net.minecraft.world.entity.Entity entity, @Nonnull NBTTagCompound compound) {
        // void load(net.minecraft.nbt.CompoundTag) -> g
        entity.g(compound);
    }

    public static void setString(@Nonnull org.bukkit.entity.Entity entity, @Nonnull String key,
            @Nullable String value) {
        CraftEntity craftEntity = (CraftEntity) entity;
        setString(Objects.requireNonNull(craftEntity.getHandle()), key, value);
    }

    public static void setString(@Nonnull net.minecraft.world.entity.Entity entity, @Nonnull String key,
            @Nullable String value) {
        NBTTagCompound compound = NBTEntityHelper.getNBT(entity);
        NBTTagCompoundHelper.put(compound, key, value);
        NBTEntityHelper.setNBT(entity, compound);
    }

    public static void merge(@Nonnull org.bukkit.entity.Entity entity, @Nonnull NBTTagCompound compound) {
        CraftEntity craftEntity = (CraftEntity) entity;
        merge(Objects.requireNonNull(craftEntity.getHandle()), compound);
    }

    public static void merge(@Nonnull net.minecraft.world.entity.Entity entity, @Nonnull NBTTagCompound compound) {
        NBTTagCompound originalCompound = getNBT(entity);
        originalCompound = NBTTagCompoundHelper.merge(originalCompound, compound);
        setNBT(entity, originalCompound);
    }
}
