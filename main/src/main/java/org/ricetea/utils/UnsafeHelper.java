package org.ricetea.utils;

import sun.misc.Unsafe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Objects;

public class UnsafeHelper {
    private static final Field unsafeInst;

    static {
        Field field;
        try {
            field = Unsafe.class.getDeclaredField("theUnsafe");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            field = null;
        }
        if (field != null)
            field.setAccessible(true);
        unsafeInst = field;
    }

    @Nonnull
    public static Unsafe getUnsafe() {
        return Objects.requireNonNull(getUnsafeUnsafely());
    }

    @Nullable
    public static Unsafe getUnsafeUnsafely() {
        if (unsafeInst == null)
            return null;
        try {
            return ObjectUtil.tryCast(unsafeInst.get(null), Unsafe.class);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
