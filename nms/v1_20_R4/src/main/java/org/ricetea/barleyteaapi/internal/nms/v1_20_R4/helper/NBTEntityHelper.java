package org.ricetea.barleyteaapi.internal.nms.v1_20_R4.helper;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.craftbukkit.entity.CraftEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public final class NBTEntityHelper {
    @Nonnull
    public static CompoundTag getNBT(@Nonnull org.bukkit.entity.Entity entity) {
        CraftEntity craftEntity = (CraftEntity) entity;
        return getNBT(Objects.requireNonNull(craftEntity.getHandle()));
    }

    @Nonnull
    public static CompoundTag getNBT(@Nonnull net.minecraft.world.entity.Entity entity) {
        // net.minecraft.nbt.CompoundTag saveWithoutId(net.minecraft.nbt.CompoundTag) -> f
        return Objects.requireNonNull(entity.saveWithoutId(new CompoundTag()));
    }

    public static void setNBT(@Nonnull org.bukkit.entity.Entity entity, @Nonnull CompoundTag compound) {
        CraftEntity craftEntity = (CraftEntity) entity;
        setNBT(Objects.requireNonNull(craftEntity.getHandle()), compound);
    }

    public static void setNBT(@Nonnull net.minecraft.world.entity.Entity entity, @Nonnull CompoundTag compound) {
        // void load(net.minecraft.nbt.CompoundTag) -> g
        entity.load(compound);
    }

    public static void setString(@Nonnull org.bukkit.entity.Entity entity, @Nonnull String key,
                                 @Nullable String value) {
        CraftEntity craftEntity = (CraftEntity) entity;
        setString(Objects.requireNonNull(craftEntity.getHandle()), key, value);
    }

    public static void setString(@Nonnull net.minecraft.world.entity.Entity entity, @Nonnull String key,
                                 @Nullable String value) {
        CompoundTag compound = NBTEntityHelper.getNBT(entity);
        NBTTagCompoundHelper.put(compound, key, value);
        NBTEntityHelper.setNBT(entity, compound);
    }

    public static void merge(@Nonnull org.bukkit.entity.Entity entity, @Nonnull CompoundTag compound) {
        CraftEntity craftEntity = (CraftEntity) entity;
        merge(Objects.requireNonNull(craftEntity.getHandle()), compound);
    }

    public static void merge(@Nonnull net.minecraft.world.entity.Entity entity, @Nonnull CompoundTag compound) {
        CompoundTag originalCompound = getNBT(entity);
        originalCompound = NBTTagCompoundHelper.merge(originalCompound, compound);
        setNBT(entity, originalCompound);
    }
}
