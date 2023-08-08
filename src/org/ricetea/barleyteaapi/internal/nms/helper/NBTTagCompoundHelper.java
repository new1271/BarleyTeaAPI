package org.ricetea.barleyteaapi.internal.nms.helper;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public final class NBTTagCompoundHelper {
    public static NBTTagCompound createNew() {
        return new NBTTagCompound();
    }

    public static boolean contains(NBTTagCompound compound, String key) {
        // boolean contains(java.lang.String) -> e
        return compound.e(key);
    }

    public static NBTTagCompound getOrAdd(NBTTagCompound compound, String key) {
        // net.minecraft.nbt.Tag get(java.lang.String) -> c
        var result = contains(compound, key) ? castCompoundSafely(compound.c(key)) : put(compound, key, createNew());
        return result;
    }

    public static boolean getBoolean(NBTTagCompound compound, String key) {
        // boolean getBoolean(java.lang.String) -> q
        return compound.q(key);
    }

    public static short getShort(NBTTagCompound compound, String key) {
        // short getShort(java.lang.String) -> g
        return compound.g(key);
    }

    public static int getInt(NBTTagCompound compound, String key) {
        // int getInt(java.lang.String) -> h
        return compound.h(key);
    }

    public static long getLong(NBTTagCompound compound, String key) {
        // long getLong(java.lang.String) -> i
        return compound.i(key);
    }

    public static String getString(NBTTagCompound compound, String key) {
        // java.lang.String getString(java.lang.String) -> l
        return compound.l(key);
    }

    public static NBTTagCompound getCompound(NBTTagCompound compound, String key) {
        // net.minecraft.nbt.CompoundTag getCompound(java.lang.String) -> p
        return compound.p(key);
    }

    public static void put(NBTTagCompound compound, String key, boolean value) {
        // void putBoolean(java.lang.String,boolean) -> a
        compound.a(key, value);
    }

    public static void put(NBTTagCompound compound, String key, short value) {
        // void putShort(java.lang.String,short) -> a
        compound.a(key, value);
    }

    public static void put(NBTTagCompound compound, String key, int value) {
        // void putInteger(java.lang.String,int) -> a
        compound.a(key, value);
    }

    public static void put(NBTTagCompound compound, String key, long value) {
        // void putLong(java.lang.String,long) -> a
        compound.a(key, value);
    }

    public static void put(NBTTagCompound compound, String key, String value) {
        // void putString(java.lang.String,java.lang.String) -> a
        compound.a(key, value);
    }

    public static NBTTagCompound put(NBTTagCompound compound, String key, NBTTagCompound value) {
        // net.minecraft.nbt.Tag put(java.lang.String,net.minecraft.nbt.Tag) -> a
        compound.a(key, value);
        return value;
    }

    public static NBTBase put(NBTTagCompound compound, String key, NBTBase value) {
        // net.minecraft.nbt.Tag put(java.lang.String,net.minecraft.nbt.Tag) -> a
        return compound.a(key, value);
    }

    public static NBTTagCompound merge(NBTTagCompound mergee, NBTTagCompound merger) {
        // net.minecraft.nbt.CompoundTag merge(net.minecraft.nbt.CompoundTag) -> a
        return mergee.a(merger);
    }

    public static NBTTagCompound castCompoundSafely(NBTBase base) {
        return base instanceof NBTTagCompound ? (NBTTagCompound) base : null;
    }
}
