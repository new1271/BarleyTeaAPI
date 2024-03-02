package org.ricetea.utils;

import javax.annotation.Nonnull;

public class EmptyArrays {

    private static final Object[] array = new Object[0];
    private static final byte[] byteArray = new byte[0];
    private static final char[] charArray = new char[0];
    private static final short[] shortArray = new short[0];
    private static final int[] intArray = new int[0];
    private static final long[] longArray = new long[0];
    private static final float[] floatArray = new float[0];
    private static final double[] doubleArray = new double[0];

    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> T[] emptyArray() {
        return (T[]) array;
    }

    @Nonnull
    public static byte[] emptyByteArray() {
        return byteArray;
    }

    @Nonnull
    public static char[] emptyCharArray() {
        return charArray;
    }

    @Nonnull
    public static short[] emptyShortArray() {
        return shortArray;
    }

    @Nonnull
    public static int[] emptyIntArray() {
        return intArray;
    }

    @Nonnull
    public static long[] emptyLongArray() {
        return longArray;
    }

    @Nonnull
    public static float[] emptyFloatArray() {
        return floatArray;
    }

    @Nonnull
    public static double[] emptyDoubleArray() {
        return doubleArray;
    }

}
