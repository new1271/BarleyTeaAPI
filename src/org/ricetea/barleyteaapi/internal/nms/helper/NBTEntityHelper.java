package org.ricetea.barleyteaapi.internal.nms.helper;

import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;

import net.minecraft.nbt.NBTTagCompound;

public final class NBTEntityHelper {
    public static NBTTagCompound getNBT(org.bukkit.entity.Entity entity) {
        CraftEntity craftEntity = (CraftEntity) entity;
        return getNBT(craftEntity.getHandle());
    }

    public static NBTTagCompound getNBT(net.minecraft.world.entity.Entity entity) {
        // net.minecraft.nbt.CompoundTag saveWithoutId(net.minecraft.nbt.CompoundTag) -> f
        return entity.f(new NBTTagCompound());
    }

    public static void setNBT(org.bukkit.entity.Entity entity, NBTTagCompound compound) {
        CraftEntity craftEntity = (CraftEntity) entity;
        setNBT(craftEntity.getHandle(), compound);
    }

    public static void setNBT(net.minecraft.world.entity.Entity entity, NBTTagCompound compound) {
        // void load(net.minecraft.nbt.CompoundTag) -> g
        entity.g(compound);
    }

    public static void setString(org.bukkit.entity.Entity entity, String key, String value) {
        CraftEntity craftEntity = (CraftEntity) entity;
        setString(craftEntity.getHandle(), key, value);
    }

    public static void setString(net.minecraft.world.entity.Entity entity, String key, String value) {
        NBTTagCompound compound = NBTEntityHelper.getNBT(entity);
        NBTTagCompoundHelper.put(compound, key, value);
        NBTEntityHelper.setNBT(entity, compound);
    }

    public static void merge(org.bukkit.entity.Entity entity, NBTTagCompound compound) {
        CraftEntity craftEntity = (CraftEntity) entity;
        merge(craftEntity.getHandle(), compound);
    }

    public static void merge(net.minecraft.world.entity.Entity entity, NBTTagCompound compound) {
        NBTTagCompound originalCompound = getNBT(entity);
        originalCompound = NBTTagCompoundHelper.merge(originalCompound, compound);
        setNBT(entity, originalCompound);
    }
}
