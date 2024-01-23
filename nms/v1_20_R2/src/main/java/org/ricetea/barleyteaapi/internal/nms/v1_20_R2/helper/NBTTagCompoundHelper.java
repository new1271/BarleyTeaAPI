package org.ricetea.barleyteaapi.internal.nms.v1_20_R2.helper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public final class NBTTagCompoundHelper {
    @Nonnull
    public static CompoundTag createNew() {
        return new CompoundTag();
    }

    public static boolean contains(@Nonnull CompoundTag compound, @Nonnull String key) {
        // boolean contains(java.lang.String) -> e
        return compound.contains(key);
    }

    @Nullable
    public static CompoundTag getOrAdd(@Nonnull CompoundTag compound, @Nonnull String key) {
        // net.minecraft.nbt.Tag get(java.lang.String) -> c
        return contains(compound, key) ? castCompoundSafely(compound.get(key)) : put(compound, key, createNew());
    }

    public static boolean getBoolean(@Nonnull CompoundTag compound, @Nonnull String key) {
        // boolean getBoolean(java.lang.String) -> q
        return compound.getBoolean(key);
    }

    public static short getShort(@Nonnull CompoundTag compound, @Nonnull String key) {
        // short getShort(java.lang.String) -> g
        return compound.getShort(key);
    }

    public static int getInt(@Nonnull CompoundTag compound, @Nonnull String key) {
        // int getInt(java.lang.String) -> h
        return compound.getInt(key);
    }

    public static long getLong(@Nonnull CompoundTag compound, @Nonnull String key) {
        // long getLong(java.lang.String) -> i
        return compound.getLong(key);
    }

    @Nonnull
    public static String getString(@Nonnull CompoundTag compound, @Nonnull String key) {
        // java.lang.String getString(java.lang.String) -> l
        return compound.getString(key);
    }

    @Nonnull
    public static CompoundTag getCompound(@Nonnull CompoundTag compound, @Nonnull String key) {
        // net.minecraft.nbt.CompoundTag getCompound(java.lang.String) -> p
        return compound.getCompound(key);
    }

    public static void put(@Nonnull CompoundTag compound, @Nonnull String key, boolean value) {
        // void putBoolean(java.lang.String,boolean) -> a
        compound.putBoolean(key, value);
    }

    public static void put(@Nonnull CompoundTag compound, String key, short value) {
        // void putShort(java.lang.String,short) -> a
        compound.putShort(key, value);
    }

    public static void put(@Nonnull CompoundTag compound, String key, int value) {
        // void putInteger(java.lang.String,int) -> a
        compound.putInt(key, value);
    }

    public static void put(@Nonnull CompoundTag compound, String key, long value) {
        // void putLong(java.lang.String,long) -> a
        compound.putLong(key, value);
    }

    public static void put(@Nonnull CompoundTag compound, String key, String value) {
        // void putString(java.lang.String,java.lang.String) -> a
        compound.putString(key, value);
    }

    @Nonnull
    public static CompoundTag put(@Nonnull CompoundTag compound, String key, @Nonnull CompoundTag value) {
        // net.minecraft.nbt.Tag put(java.lang.String,net.minecraft.nbt.Tag) -> a
        compound.put(key, value);
        return value;
    }

    @Nullable
    public static Tag put(CompoundTag compound, String key, @Nonnull Tag value) {
        // net.minecraft.nbt.Tag put(java.lang.String,net.minecraft.nbt.Tag) -> a
        return compound.put(key, value);
    }

    @Nonnull
    public static CompoundTag merge(@Nonnull CompoundTag mergee, @Nonnull CompoundTag merger) {
        // net.minecraft.nbt.CompoundTag merge(net.minecraft.nbt.CompoundTag) -> a
        return Objects.requireNonNull(mergee.merge(merger));
    }

    @Nullable
    public static CompoundTag castCompoundSafely(@Nullable Tag base) {
        return base instanceof CompoundTag ? (CompoundTag) base : null;
    }
}
