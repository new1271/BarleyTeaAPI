package org.ricetea.utils;

public class MathHelper {
    public static int between(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public static long between(long value, long min, long max) {
        return Math.min(Math.max(value, min), max);
    }

    public static float between(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    public static double between(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }
}
