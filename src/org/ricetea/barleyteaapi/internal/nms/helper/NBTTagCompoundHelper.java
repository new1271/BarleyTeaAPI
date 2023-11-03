package org.ricetea.barleyteaapi.internal.nms.helper;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public final class NBTTagCompoundHelper {
    @Nonnull
    public static NBTTagCompound createNew() {
        return new NBTTagCompound();
    }

    public static boolean contains(@Nonnull NBTTagCompound compound, @Nonnull String key) {
        // boolean contains(java.lang.String) -> e
        return compound.e(key);
    }

    @Nullable
    public static NBTTagCompound getOrAdd(@Nonnull NBTTagCompound compound, @Nonnull String key) {
        // net.minecraft.nbt.Tag get(java.lang.String) -> c
        var result = contains(compound, key) ? castCompoundSafely(compound.c(key)) : put(compound, key, createNew());
        return result;
    }

    public static boolean getBoolean(@Nonnull NBTTagCompound compound, @Nonnull String key) {
        // boolean getBoolean(java.lang.String) -> q
        return compound.q(key);
    }

    public static short getShort(@Nonnull NBTTagCompound compound, @Nonnull String key) {
        // short getShort(java.lang.String) -> g
        return compound.g(key);
    }

    public static int getInt(@Nonnull NBTTagCompound compound, @Nonnull String key) {
        // int getInt(java.lang.String) -> h
        return compound.h(key);
    }

    public static long getLong(@Nonnull NBTTagCompound compound, @Nonnull String key) {
        // long getLong(java.lang.String) -> i
        return compound.i(key);
    }

    @Nullable
    public static String getString(@Nonnull NBTTagCompound compound, @Nonnull String key) {
        // java.lang.String getString(java.lang.String) -> l
        return compound.l(key);
    }

    @Nullable
    public static NBTTagCompound getCompound(@Nonnull NBTTagCompound compound, @Nonnull String key) {
        // net.minecraft.nbt.CompoundTag getCompound(java.lang.String) -> p
        return compound.p(key);
    }

    public static void put(@Nonnull NBTTagCompound compound, @Nonnull String key, boolean value) {
        // void putBoolean(java.lang.String,boolean) -> a
        compound.a(key, value);
    }

    public static void put(@Nonnull NBTTagCompound compound, String key, short value) {
        // void putShort(java.lang.String,short) -> a
        compound.a(key, value);
    }

    public static void put(@Nonnull NBTTagCompound compound, String key, int value) {
        // void putInteger(java.lang.String,int) -> a
        compound.a(key, value);
    }

    public static void put(@Nonnull NBTTagCompound compound, String key, long value) {
        // void putLong(java.lang.String,long) -> a
        compound.a(key, value);
    }

    public static void put(@Nonnull NBTTagCompound compound, String key, String value) {
        // void putString(java.lang.String,java.lang.String) -> a
        compound.a(key, value);
    }

    @Nullable
    public static NBTTagCompound put(@Nonnull NBTTagCompound compound, String key, @Nullable NBTTagCompound value) {
        // net.minecraft.nbt.Tag put(java.lang.String,net.minecraft.nbt.Tag) -> a
        compound.a(key, value);
        return value;
    }

    @Nullable
    public static NBTBase put(NBTTagCompound compound, String key, @Nullable NBTBase value) {
        // net.minecraft.nbt.Tag put(java.lang.String,net.minecraft.nbt.Tag) -> a
        return compound.a(key, value);
    }

    @Nonnull
    public static NBTTagCompound merge(@Nonnull NBTTagCompound mergee, @Nonnull NBTTagCompound merger) {
        // net.minecraft.nbt.CompoundTag merge(net.minecraft.nbt.CompoundTag) -> a
        return Objects.requireNonNull(mergee.a(merger));
    }

    @Nullable
    public static NBTTagCompound castCompoundSafely(@Nullable NBTBase base) {
        return base instanceof NBTTagCompound ? (NBTTagCompound) base : null;
    }
}
